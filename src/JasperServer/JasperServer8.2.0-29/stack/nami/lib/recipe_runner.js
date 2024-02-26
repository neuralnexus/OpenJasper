"use strict";
/*
 * Copyright VMware, Inc.
 * SPDX-License-Identifier: GPL-2.0-only
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.RecipeRunner = void 0;
const fs = require("fs");
const path = require("path");
const glob = require("glob");
const _ = require("lodash");
;
;
;
;
;
/**
 * Class for running recipes
 */
class RecipeRunner {
    /**
     * Consturctor
     * @param options Options to create the runner with
     */
    constructor(options) {
        this.recipesDirectory = path.join(path.dirname(module.filename), "recipes");
        this.provisioner = options.provisioner;
        this._storageName = options.storageName;
        this.logger = this.provisioner.logger;
        this.reset();
    }
    /**
     * Remove all recipes from the runner
     */
    reset() {
        // reset to empty recipe runner
        this._solution = undefined;
        this._layeredSolution = undefined;
        this._eventName = undefined;
        this.recipes = [];
    }
    /**
     * Load built-in recipes along with optional custom directory ;
     * all recipe files are read and they should call `recipes.register` only, not perform
     * any actions directly at load time
     * @param customDirectory If specified, recipes in the directory are also read, recursively
     */
    loadInternalDefinitions(customDirectory) {
        // load definitions from definitions subdirectory
        for (let directory of [this.recipesDirectory, customDirectory]) {
            if (directory) {
                if (fs.existsSync(directory)) {
                    this.loadFromDirectory(directory);
                }
                else {
                    this.logger.warn(`Unable to load recipes from ${directory}`);
                }
            }
        }
        this.logger.trace("recipeRunner: loaded default definitions", () => {
            return {
                recipes: this.recipes.map((recipe) => { return recipe.id; }).sort()
            };
        });
    }
    /**
     * Load all recipes from a directory ;
     * all recipe files are read and they should call `recipes.register` only, not perform
     * any actions directly at load time
     * @param path Directory to read recipes from, recursively
     * @param opts Not currently used
     */
    loadFromDirectory(path, opts) {
        // load all definitions recursively
        const _previousRecipes = this.recipes;
        let files = glob.sync("**/*.js", { cwd: path, absolute: true });
        files.sort();
        try {
            for (let file of files) {
                this.logger.trace(`Loading recipes from ${file}`);
                this.loadFromFile(file, opts);
            }
        }
        catch (e) {
            this.reset();
            this.recipes = _previousRecipes;
            throw e;
        }
    }
    /**
     * Load recipe from a file
     * all recipe files are read and they should call `recipes.register` only, not perform
     * any actions directly at load time
     */
    loadFromFile(fileName, opts) {
        this.loadFromCode(fs.readFileSync(fileName).toString(), Object.assign({
            fileName: fileName
        }, opts || {}));
    }
    /**
     * Load recipe from inline code
     * all recipe files are read and they should call `recipes.register` only, not perform
     * any actions directly at load time
     */
    loadFromCode(code, opts) {
        const _previousRecipes = this.recipes;
        try {
            this.provisioner.evalInContext(code, opts);
        }
        catch (e) {
            this.reset();
            this.recipes = _previousRecipes;
            throw e;
        }
    }
    /**
     * Register a recipe ; this is the main interface for recipe files to add themselves to the runner
     * that is currently loading the recipe file
     * @param item Recipe to register
     */
    register(item) {
        // register a recipe
        this.recipes.push(item);
    }
    _getDependencies(obj) {
        // get dependencies, based on currently evaluated event"s name
        return (obj.on[this._eventName] || {}).depends || [];
    }
    _getProvidedDependencies(obj) {
        // get provided items, based on currently evaluated event"s name
        return (obj.on[this._eventName] || {}).provides || [];
    }
    _getRecipesForEventName(options) {
        // get all recipes that should be run for current event name
        let result = [];
        for (let recipe of this.recipes) {
            if (recipe.on &&
                (recipe.on[this._eventName])) {
                result.push(recipe);
            }
        }
        return result;
    }
    _mapDepends(obj) {
        // recursively map all dependencies for a recipe
        const value = this._getDependencies(obj);
        let allValues = [];
        for (let id of value) {
            if (!(this._allDepends[id])) {
                this._mapDepends(this._dependencyMap[id]);
            }
            allValues = allValues.concat(this._allDepends[id]);
            allValues.push(id);
        }
        for (let id of this._allProvides[obj.id]) {
            this._allDepends[id] = allValues;
        }
    }
    _mapProvides(obj) {
        // map all items a dependency is providing
        const value = this._getProvidedDependencies(obj);
        for (const provided of value) {
            this._providerIdMap[provided] = obj.id;
        }
        this._allProvides[obj.id] = [obj.id].concat(value);
    }
    _allProvided(provided, obj) {
        // check if all dependencies required for a recipe are already provided
        let result = true;
        for (let dep of this._getDependencies(obj)) {
            if (provided.indexOf(dep) < 0) {
                result = false;
                break;
            }
        }
        return result;
    }
    _shouldRunRecipe(recipe, options) {
        return (((!options.only) || (options.only.indexOf(recipe.id) >= 0)) &&
            ((!options.skip) || (options.skip.indexOf(recipe.id) < 0)));
    }
    _findSolution(options) {
        // find a solution (as in an appropriate order of running events)
        const recipes = this._getRecipesForEventName(options);
        let level = 0;
        this._allDepends = {};
        this._allProvides = {};
        this._providerIdMap = {};
        this._dependencyLevels = {};
        this._dependencyMap = {};
        // map all dependencies and provided pseudo-recipes
        for (let dependency of recipes) {
            if (this._dependencyMap[dependency.id]) {
                throw new Error(`RecipeWithDependency with id ${dependency.id} duplicated`);
            }
            this._dependencyMap[dependency.id] = dependency;
        }
        for (let dependency of recipes) {
            this._mapProvides(dependency);
        }
        for (let dependency of recipes) {
            this._mapDepends(dependency);
        }
        // iterate through all recipes and map them into layered and flat solution
        let remaining = recipes;
        let nowRemaining;
        let provided;
        let nowProvided;
        let levelSolution;
        nowProvided = [];
        provided = [];
        this._layeredSolution = [];
        this._solution = [];
        while (level < recipes.length) {
            nowRemaining = [];
            nowProvided = provided;
            levelSolution = [];
            for (let obj of remaining) {
                if (this._allProvided(provided, obj)) {
                    if (this._shouldRunRecipe(obj, options)) {
                        levelSolution.push(obj);
                        this._solution.push(obj);
                    }
                    nowProvided = nowProvided.concat(this._allProvides[obj.id]);
                }
                else {
                    nowRemaining.push(obj);
                }
            }
            this._layeredSolution.push(levelSolution);
            level += 1;
            remaining = nowRemaining;
            provided = nowProvided;
        }
        // if ran enough loops and still items remaining, dependencies are missing
        if (remaining.length > 0) {
            throw new Error("Unable to resolve dependencies - " + remaining.join(" ") +
                "recipes do not have matching providers");
        }
    }
    async _waitForDependentRecipes(id, promiseMap) {
        // wait for all dependent recipes that were  still not awaited for
        let allPromises = [];
        for (let dependentId of this._allDepends[id]) {
            if (this._providerIdMap[dependentId]) {
                dependentId = this._providerIdMap[dependentId];
            }
            if (promiseMap[dependentId]) {
                this.logger.trace("recipeRunner: waiting for recipe", dependentId);
                allPromises.push(promiseMap[dependentId]);
                promiseMap[dependentId] = undefined;
            }
        }
        if (allPromises.length > 0) {
            try {
                await Promise.all(allPromises);
            }
            catch (e) {
                this.logger.error(`Unable to wait for dependencies for ${id}`, e);
            }
        }
    }
    ;
    _checkAllAny(values, query) {
        // check if the all|any condition matches a list of values
        // if a string or list is passed, we can fall back to any condition
        if (!(query.all || query.any || query.not)) {
            query = { any: query };
        }
        if (query.all) {
            for (let item of query.all) {
                if (values.indexOf(item) < 0) {
                    return false;
                }
            }
        }
        if (query.any) {
            let list = query.any;
            if (!Array.isArray(list)) {
                list = [list];
            }
            for (let item of list) {
                if (values.indexOf(item) >= 0) {
                    return true;
                }
            }
            return false;
        }
        if (query.not) {
            if (this._checkAllAny(values, query.not)) {
                return false;
            }
        }
        return true;
    }
    _getTierModulesNames() {
        let result = [];
        for (let module of this.provisioner.tierDefinition.modules) {
            result.push(module.name);
        }
        return result;
    }
    _getStackTags() {
        return this.provisioner.stackDefinition.tags;
    }
    _getInstanceTier() {
        return this.provisioner.instanceTier;
    }
    _getTierTags() {
        return this.provisioner.tierDefinition.tags;
    }
    async _shouldCallRecipe(recipe, recipeInput, recipeOutput) {
        if (recipe.conditions) {
            if (recipe.conditions.cloudNames &&
                !(this._checkAllAny([this.provisioner.cloudName], { any: recipe.conditions.cloudNames }))) {
                this.logger.trace("recipeRunner: invalid cloud name condition for", recipe.id);
                return false;
            }
            if (recipe.conditions.cloudTags &&
                !(this._checkAllAny(this.provisioner.cloud.cloudTags, recipe.conditions.cloudTags))) {
                this.logger.trace("recipeRunner: invalid cloud tags condition for", recipe.id);
                return false;
            }
            if (recipe.conditions.platformTags &&
                !(this._checkAllAny(this.provisioner.platform.platformTags, recipe.conditions.platformTags))) {
                this.logger.trace("recipeRunner: invalid platform tags condition for", recipe.id);
                return false;
            }
            if (recipe.conditions.tierModules &&
                !(this._checkAllAny(this._getTierModulesNames(), recipe.conditions.tierModules))) {
                this.logger.trace("recipeRunner: invalid modules condition for", recipe.id);
                return false;
            }
            if (recipe.conditions.stackTags &&
                !(this._checkAllAny(this.provisioner.stackDefinition.tags, recipe.conditions.stackTags))) {
                this.logger.trace("recipeRunner: invalid stack tags for", recipe.id);
                return false;
            }
            if (recipe.conditions.instanceTier &&
                !(this._checkAllAny([this._getInstanceTier()], recipe.conditions.instanceTier))) {
                this.logger.trace("recipeRunner: invalid instance tier for", recipe.id);
                return false;
            }
            if (recipe.conditions.tierTags &&
                !(this._checkAllAny(this.provisioner.tierDefinition.tags, recipe.conditions.tierTags))) {
                this.logger.trace("recipeRunner: invalid tier tags for", recipe.id);
                return false;
            }
            if (recipe.conditions.shouldInvoke) {
                try {
                    let result = recipe.conditions.shouldInvoke(recipeInput);
                    if (result instanceof Promise) {
                        result = await result;
                    }
                    if (!result) {
                        this.logger.trace("recipeRunner: recipe shouldInvoke false for", recipe.id);
                        return false;
                    }
                }
                catch (e) {
                    recipeOutput.failed.push(recipe.id);
                    this.logger.warn(`Unable to call shouldInvoke for recipe ${recipe.id}`, e);
                    return false;
                }
            }
            if (recipe.conditions.ifChanged) {
                try {
                    recipeInput.oldValue = this._storageData.data.recipeValue[recipe.id];
                    recipeInput.newValue = recipe.conditions.ifChanged(recipeInput);
                    if (recipeInput.newValue instanceof Promise) {
                        recipeInput.newValue = await recipeInput.newValue;
                    }
                    if (recipeInput.newValue === recipeInput.oldValue) {
                        this.logger.trace("recipeRunner: recipe ifChanged unchanged", recipe.id);
                        return false;
                    }
                }
                catch (e) {
                    recipeOutput.failed.push(recipe.id);
                    this.logger.warn(`Unable to call shouldInvoke for recipe ${recipe.id}`, e);
                    return false;
                }
            }
        }
        return true;
    }
    _createRecipeInput(recipe) {
        return ({
            provisioner: this.provisioner,
            platform: this.provisioner.platform,
            eventName: this._eventName,
            recipe: recipe
        });
    }
    async _callRecipe(recipe, recipeInput) {
        let result;
        this.logger.trace("recipeRunner: calling recipe", recipe.id);
        result = recipe.recipeHandler(recipeInput);
        if (result instanceof Promise) {
            result = await result;
        }
        if (recipeInput.newValue || recipeInput.oldValue) {
            this._storageData.data.recipeValue[recipe.id] = recipeInput.newValue;
            this._storageData.save();
        }
    }
    /**
     * Call already loaded recipes using specified event name ; returns object with summary
     * @param eventName Name of the event
     * @param options Options for running the recipes
     */
    async call(eventName, options) {
        let _promiseMap = {};
        let result = {
            invoked: [],
            successful: [],
            failed: []
        };
        options = Object.assign({}, options);
        if (!this._storageData) {
            this._storageData = this.provisioner.storageManager.getItem(this._storageName);
            // initialize storage
            this._storageData.data.recipeValue = this._storageData.data.recipeValue || {};
        }
        // if not checked solution or solution for this event name, find one
        if (!this._solution || (this._eventName !== eventName)) {
            this._eventName = eventName;
            this._findSolution(options);
        }
        for (let obj of this._solution) {
            const recipeInput = this._createRecipeInput(obj);
            await this._waitForDependentRecipes(obj.id, _promiseMap);
            if (await this._shouldCallRecipe(obj, recipeInput, result)) {
                result.invoked.push(obj.id);
                try {
                    let p = this._callRecipe(obj, recipeInput);
                    if (p instanceof Promise) {
                        if (obj.forceAwait) {
                            this.logger.trace("recipeRunner: forceAwait for recipe", obj.id);
                            await p;
                        }
                        else {
                            this.logger.trace("recipeRunner: storing promise for recipe", obj.id);
                            _promiseMap[obj.id] = p;
                        }
                    }
                    else {
                        this.logger.trace("recipeRunner: successful invocation for recipe", obj.id);
                    }
                }
                catch (e) {
                    result.failed.push(obj.id);
                    // TODO: improve logging
                    this.logger.warn(`Unable to run recipe ${obj.id}`, e);
                }
            }
        }
        for (let key of Object.keys(_promiseMap)) {
            const promise = _promiseMap[key];
            this.logger.trace("recipeRunner: Awaiting pending async promise", key);
            try {
                await promise;
            }
            catch (e) {
                result.failed.push(key);
                this.logger.warn(`Unable to run recipe ${key}`, e);
            }
        }
        result.successful = _.difference(result.invoked, result.failed);
        this.logger.trace("recipeRunner: Finished running recipes for", eventName, result);
        if (result.failed.length > 0 && options.errorOnFail) {
            throw new Error(`Unable to run recipes: one or more recipes failed`);
        }
        return result;
    }
}
exports.RecipeRunner = RecipeRunner;
