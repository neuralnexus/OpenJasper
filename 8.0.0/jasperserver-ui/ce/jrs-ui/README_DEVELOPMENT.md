# Advanced developer documentation

This document contains advanced capabilities which are usually needed only by JRS developers.

For the basic documentation please read [./README.md](./README.md)

Table of Contents:

- [NPM scripts](#npm-scripts)
    - [Common npm scripts](#common-npm-scripts)
    - [jrs-ui specific scripts](#jrs-ui-specific-scripts)
    - [Understanding NPM scripts](#jrs-ui-specific-scripts)
        - [Npm-run-all](#npm-run-all)
        - [dotenv](#dotenv)
        - [Environment variables in npm scripts](#environment-variables-in-npm-scripts)
- [Lerna](#lerna) 
    - [Automate running npm scripts in faf modules](#automate-running-npm-scripts-in-faf-modules)       
    - [Automate running shell commands in faf modules](#automate-running-shell-commands-in-faf-modules)
- [Add/Remove/Upgrade npm libraries](#addremoveupgrade-npm-libraries)
    - [Automate Add/Remove/Upgrade npm libraries in all/certain faf modules](#automate-addremoveupgrade-npm-libraries-in-allcertain-faf-modules)           

## NPM scripts
`package.json` file in each `faf-module` (like `js-sdk`, `jrs-ui` etc.) contains `scripts` property
which contains [npm scripts](https://docs.npmjs.com/cli/v6/using-npm/scripts) available for this specific `faf-module`.

Some scripts are common for all `faf-modules` 
while other are specific only for this module.

To run any of the `npm scripts` mentioned below use the following syntax:
```shell script
yarn run <name>
```
where `<name>` is the name of npm script

### Common npm scripts
- `test` - run unit tests
- `test:debug` - run unit tests in a browser with an ability to debug them
- `lint` - run linting of the source code. In `jrs-ui` it also runs 3-rd party libraries check for dangerous code. 
- `depVersionCheck` - run 3-rd party libs version check.
- `jsdoc` - generate documentation based on the source code comments
- `default` - run default commands sequence before code commit.

### jrs-ui specific scripts
- `clean` - remove `build` folder 
- `build` - run build process which will prepare distribution ready for production (see [./README.md#build](./README.md#build)) 
- `deploy` - copy result of the source code build to the JRS instance. Additional configutations is needed (see [./README.md#deploy](./README.md#deploy)) 
- `start` - start Webpack Dev Server (see [./README.md#rapid-development-environment])
- `start:themes` - start Webpack Dev Server with themes livereload (see [./README.md#develop-default-theme])
- `audit` - run vulnerabilities check of the 3-rd party libraries
- `overlay` - copy themes and bundles from all `faf-modules` to aggregate them before deploying to the JRS instance. 
- `mvnPublish` - deploy build result to the local maven repository. 
    Useful when WEB UI build done **before** JRS backend build (see [Deploy to local maven repository](./README.md#deploy-to-local-maven-repository))

### Understanding NPM scripts
In order to Add/Modify npm scripts we have to understand how they work
and JRS-specifics of npm scripts.

First of all read [npm scripts](https://docs.npmjs.com/cli/v6/using-npm/scripts).

in JRS we use two handy npm libraries which allows us to write npm scripts much effective:
- [npm-run-all](https://www.npmjs.com/package/npm-run-all)
- [dotenv](https://www.npmjs.com/package/dotenv)

#### Npm-run-all
Allows us to use the following commands in npm scripts:
- `run-s` - allows us to run other npm scripts serially.
    For example the following npm script:
    ```shell script
    run-s test lint
    ```
  will run `test` and `lint` npm scripts one by one
- `run-p` - allows us to run other npm scripts in parallel
    For example the following npm script:
    ```shell script
    run-p test lint
    ```
  will run `test` and `lint` npm scripts in parallel

#### dotenv
`Dotenv` allows us to use `.env` file to declare all environment variables
instead of necessity to set them as a real env variables in the command shell.
We use [dotenv-cli](https://www.npmjs.com/package/dotenv-cli) library which allows us to use `dotenv` library in npm scripts.
For example let we have the following script `js-sdk/scripts/some-script.js`:
```js
console.log(process.env.SOME_ENV_VARIABLE)
```
Also we added `SOME_ENV_VARIABLE` to the `.env` file in the local folder:
```properties
SOME_ENV_VARIABLE=some value
```
Finally, we added new npm script called `envtest` to the local `package.json`:
```json
{
  "envtest": "dotenv -c -- node node_modules/js-sdk/scripts/some-script"
}
```
Now if we will run `yarn run envtest` we will see the following in the console:
```text
some value
```

#### Environment variables in npm scripts
It is possible to use environment variables in npm script command line.

For example if we set the following env var: `SOME_ENV_VARIABLE=some value`
we can use this env var in npm script:
```json
{
  "envtest": "echo $SOME_ENV_VARIABLE"
}
```
This will output value of the SOME_ENV_VARIABLE env var to console.

As you might [remember](../README.md#set-up) we configured npm to use `git bash` to run our npm scripts.
This allows us to write npm scripts as we are in unix environment. Thanks to git bash this will work on windows too. 

Because of this we can use extended syntax of passing env vars to npm scripts:
```json
{
  "envtest": "echo ${SOME_ENV_VARIABLE:-test}"
}
```
This syntax means that if `SOME_ENV_VARIABLE` env var is not set - the default `test` value will be used.

##### Using env vars in npm scripts with dotenv
It would be very helpful if we can set values of the env vars defined in our npm scripts from the `.env` file.
Unfortunately this syntax will not work:
```json
{
  "envtest": "dotenv -c -- echo $SOME_ENV_VARIABLE"
}
```
The problem is that whole scripts command `dotenv -c -- echo $SOME_ENV_VARIABLE` parsed by the shell 
**before** dotenv command executed.
But we can workaround this by adding second npm script:
```json
{
  "echo": "echo $SOME_ENV_VARIABLE",
  "envtest": "dotenv -c -- run-s echo"
}
```
Now if we run `npm run envtest` we will see value of the SOME_ENV_VARIABLE printed to the console.

## Lerna
We use [Lerna.js](https://lerna.js.org) to organize a multi-module project.

Lerna helps to symlink one `faf-modules` in other `faf-modules`.
For example in `jrs-ui` there is a dependency declared in `package.json`:
```json
{
  "bi-report": "8.0.0"
}
``` 
However, there is no `bi-report` in the npm repository.
During installing npm dependencies, lerna creates a symlink `jrs-ui/node_modules/bi-report`
which references real path to `bi-report` folder so it become possible to use code from `bi-report`
in `jrs-ui`.

### Automate running npm scripts in faf modules
Lerna also allows us to automate running npm scripts in different faf modules.
For example, if we want to run `lint` npm script in all faf modules we can run the following command:
> NOTE: you should run lerna command from the folder which contains `lerna.json`

```shell script
npx lerna run lint
```
By default `Lerna` uses topological order to run scripts in correct order.
For JRS WEB UI it's true that **any** npm scripts could be executed in parallel in all faf modules.
So we can use the following syntax:
```shell script
npx lerna run --stream --parallel lint
```
- `--parallel` parameter disables topological order and enables real parallel execution
- `--stream` interleaves log output so you can see output from all faf modules immediately in console

if you want to run this command only in certain faf modules, let's say only in `jrs-ui` and in `bi-report`
use the following syntax:
```shell script
npx lerna run --stream --parallel --scope jrs-ui --scope bi-report lint
```
### Automate running shell commands in faf modules
`Lerna` also allows us to run any arbitrary shell command in all or certain faf modules.

For example to remove `node_modules` from all faf modules run this command:
> NOTE: you should run lerna command from the folder which contains `lerna.json`
```shell script
npx lerna exec --stream --parallel -- rm -rf node_modules
```

To remove `node_modules` from `jrs-ui` and `bi-report` run this command:
> NOTE: you should run lerna command from the folder which contains `lerna.json`
```shell script
npx lerna exec --stream --parallel --scope jrs-ui --scope bi-report -- rm -rf node_modules
```

## Add/Remove/Upgrade npm libraries
`Lerna` has ability to add new npm library, but unfortunately it has no ability
to remove/upgrade npm library.

At the other hand by default we cannot run usual `yarn` commands to 
add/remove/upgrade libraries, because we changed `package.json` so
`faf-modules` are referenced like so (see [Lerna](#lerna)):
```json
{
  "bi-report": "8.0.0"
}
``` 
Yarn will complain that `bi-report` is not available in the remote npm registry.

To workaround this we introduced special npm script `jsyarn` which
temporarily patch `package.json` so yarn understands it, runs necessary yarn command
and then rolls back patched `package.json` to the original state.

The syntax of this command is the following
(you should run it form any of the faf-modules folder):
```shell script
yarn run jsyarn <all normal yarn commands and flags>
```
For example if you want to upgrade `webpack` library in `jrs-ui` module,
run this command from the `jrs-ui` folder:
```shell script
yarn run jsyarn upgrade webpack@latest
```
### Automate Add/Remove/Upgrade npm libraries in all/certain faf modules
What if we want to modify some library in all faf modules?
`jsyarn` npm script will help us here as well.
For example if you want to upgrade `webpack` in all faf modules you 
can run this command (you should run it from the folder which contains `lerna.json`):
```shell script
yarn run jsyarn upgrade webpack@latest
```

But if you want to upgrade `webpack` only in `jrs-ui` and in `bi-report`
you should use `Lerna (see [Automate running npm scripts in faf modules](#automate-running-npm-scripts-in-faf-modules)):

```shell script
npx lerna run jsyarn --stream --scope jrs-ui --scope bi-report -- upgrade webpack@latest
```
