/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: TableColumnSchemaTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var json3 = require("json3"),
        tv4 = require("tv4"),
        interactiveComponentTypes = require("report/jive/enum/interactiveComponentTypes"),
        tableColumnSchema = json3.parse(require("text!bi/component/schema/TableColumn.json"));

    describe("TableColumn JSON schema", function() {
        describe("'filter' tests", function() {
            describe("'string' dataType", function() {
                var dataType = "string";

                it("should be valid with correct 'operator' and 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "contain",
                                "value": "aaa"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty filter", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'operator' or 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "contain",
                                "value": 123
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'boolean' dataType", function() {
                var dataType = "boolean";

                it("should be valid with correct 'operator' and 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": true
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty filter", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'operator' or 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": 123
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'numeric' dataType", function() {
                var dataType = "numeric";

                it("should be valid with correct 'operator' and 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": 1
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj = {
                        "id": "test",
                        "dataType": dataType,
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "filter": {
                            "operator": "between",
                            "value": [1,2]
                        }
                    };
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty filter", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'operator' or 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": "123"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);

                    obj = {
                        "id": "test",
                        "dataType": dataType,
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "filter": {
                            "operator": "between",
                            "value": [1,2,3]
                        }
                    };
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'datetime' dataType", function() {
                var dataType = "datetime";

                it("should be valid with correct 'operator' and 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": "2012-05-04T13:45:50"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj = {
                        "id": "test",
                        "dataType": dataType,
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "filter": {
                            "operator": "between",
                            "value": ["2012-05-04T13:45:50", "2014-05-04T13:45:51"]
                        }
                    };
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty filter", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'operator' or 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": "2012-05-04 13:45:50"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);

                    obj = {
                        "id": "test",
                        "dataType": dataType,
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "filter": {
                            "operator": "between",
                            "value": ["2012-05-04T13:45:50Z", 2]
                        }
                    };
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'time' dataType", function() {
                var dataType = "time";

                it("should be valid with correct 'operator' and 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": "13:45:50"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj = {
                        "id": "test",
                        "dataType": dataType,
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "filter": {
                            "operator": "between",
                            "value": ["13:45:50", "13:45:51"]
                        }
                    };
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty filter", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'operator' or 'value'", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "filter": {
                                "operator": "equal",
                                "value": "13-45-50"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);

                    obj = {
                        "id": "test",
                        "dataType": dataType,
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "filter": {
                            "operator": "between",
                            "value": ["13-45-50", "134550"]
                        }
                    };
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });
        });

        describe("'sort' tests", function() {
            it("should be valid with correct 'order'", function() {
                var obj = {
                        "id": "test",
                        "dataType": "string",
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "sort": {
                            "order": "asc"
                        }
                    },
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(true);

                obj.sort.order = "desc";

                result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(true);
            });

            it("should be valid with empty sorting", function() {
                var obj = {
                        "id": "test",
                        "dataType": "string",
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "sort": {}
                    },
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(true);
            });

            it("should be invalid with incorrect 'order'", function() {
                var obj = {
                        "id": "test",
                        "dataType": "string",
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "sort": {
                            "order": "sdfsdf"
                        }
                    },
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(false);

                obj.sort.order = null;

                result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(false);
            });
        });

        describe("'headingFormat' tests", function() {
            it("should be valid with correct 'headingFormat'", function() {
                var obj = {
                        "id": "test",
                        "dataType": "string",
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "headingFormat": {
                            "backgroundColor": "transparent",
                            "align": "left",
                            "font": {
                                "name": "Monospace",
                                "size": 10.5,
                                "bold": true,
                                "italic": true,
                                "underline": true,
                                "color": "ffee00"
                            }
                        }
                    },
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(true);

                obj.headingFormat.backgroundColor = "00ffee";

                result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(true);
            });

            it("should be valid with empty 'headingFormat'", function() {
                var obj = {
                        "id": "test",
                        "dataType": "string",
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "headingFormat": {}
                    },
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(true);
            });

            it("should be invalid with incorrect 'headingFormat'", function() {
                var obj = {
                        "id": "test",
                        "dataType": "string",
                        "componentType": interactiveComponentTypes.TABLE_COLUMN,
                        "headingFormat": {
                            "backgroundColor": "XXX",
                            "align": "AAleft",
                            "font": {
                                "name": "Monospace",
                                "size": "22",
                                "bold": true,
                                "italic": true,
                                "underline": true,
                                "color": "ffeeX00"
                            }
                        }
                    },
                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                expect(result.valid).toBe(false);
            });
        });

        describe("'detailsRowFormat' tests", function() {
            describe("'string' dataType", function() {
                it("should be valid with correct 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "string",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                }
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj.detailsRowFormat.backgroundColor = "00ffee";

                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "string",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "string",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "XXX",
                                "align": "AAleft",
                                "font": {
                                    "name": "Monospace",
                                    "size": "22",
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffeeX00"
                                }
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'boolean' dataType", function() {
                it("should be valid with correct 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "boolean",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                }
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj.detailsRowFormat.backgroundColor = "00ffee";

                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "boolean",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "boolean",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "XXX",
                                "align": "AAleft",
                                "font": {
                                    "name": "Monospace",
                                    "size": "22",
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffeeX00"
                                }
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'time' dataType", function() {
                it("should be valid with correct 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "time",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                },
                                "pattern": "hh:mm aaa"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj.detailsRowFormat.pattern = "HH:mm";

                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "time",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "time",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                },
                                "pattern": "hh:mmXXX"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'datetime' dataType", function() {
                it("should be valid with correct 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "datetime",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                },
                                "pattern": "dd/MM/yyyy"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj.detailsRowFormat.pattern = "MMM";

                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "datetime",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "datetime",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                },
                                "pattern": "hh:mm"
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'numeric' dataType", function() {
                it("should be valid with correct 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "numeric",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                },
                                "pattern": {
                                    "negativeFormat": "###0-",
                                    "grouping": true,
                                    "percentage": true,
                                    "precision": 2
                                }
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj.detailsRowFormat.pattern.currency = "LOCALE_SPECIFIC";

                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);

                    obj.detailsRowFormat.pattern.currency = "GBP";

                    result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "numeric",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {}
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect 'detailsRowFormat'", function () {
                    var obj = {
                            "id": "test",
                            "dataType": "numeric",
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "detailsRowFormat": {
                                "backgroundColor": "transparent",
                                "align": "left",
                                "font": {
                                    "name": "Monospace",
                                    "size": 10.5,
                                    "bold": true,
                                    "italic": true,
                                    "underline": true,
                                    "color": "ffee00"
                                },
                                "pattern": {
                                    "negativeFormat": "###0-",
                                    "grouping": true,
                                    "percentage": true,
                                    "precision": 2,
                                    "currency": "ABC"
                                }
                            }
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });
        });

        describe("'conditions' tests", function() {
            describe("'string' dataType", function() {
                var dataType = "string";

                it("should be valid with correct conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "contain",
                                    "value": "aaa",
                                    "backgroundColor": null,
                                    "font": {
                                        "bold": true,
                                        "italic": false,
                                        "underline": true,
                                        "color": "FF0000"
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": []
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "contain",
                                    "value": 123,
                                    "backgroundColor": "XXXXX",
                                    "font": {
                                        "bold": true,
                                        "italic": false,
                                        "underline": true,
                                        "color": "FF0000"
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'boolean' dataType", function() {
                var dataType = "boolean";

                it("should be valid with correct conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "equal",
                                    "value": false,
                                    "backgroundColor": "transparent",
                                    "font": {
                                        "bold": true,
                                        "italic": false,
                                        "underline": true,
                                        "color": "FF0000"
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": []
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "contain",
                                    "value": true,
                                    "backgroundColor": null,
                                    "font": {
                                        "bold": true,
                                        "italic": false,
                                        "underline": true,
                                        "color": "FF0000"
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'numeric' dataType", function() {
                var dataType = "numeric";

                it("should be valid with correct conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "equal",
                                    "value": 20,
                                    "backgroundColor": "AABBCC",
                                    "font": {
                                        "color": "FFFFFF",
                                        "underline": true,
                                        "bold": true,
                                        "italic": true
                                    }
                                },
                                {
                                    "operator": "between",
                                    "value": [10,19],
                                    "backgroundColor": null,
                                    "font": {
                                        "color": "FF00FF",
                                        "underline": false,
                                        "bold": false,
                                        "italic": false
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": []
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "equal",
                                    "value": "20",
                                    "backgroundColor": "AABBCC",
                                    "font": {
                                        "color": "FFFFFF",
                                        "underline": true,
                                        "bold": true,
                                        "italic": true
                                    }
                                },
                                {
                                    "operator": "between",
                                    "value": [10,19],
                                    "backgroundColor": "XXXXXX",
                                    "font": {
                                        "color": "FF00FF",
                                        "underline": false,
                                        "bold": false,
                                        "italic": false
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'datetime' dataType", function() {
                var dataType = "datetime";

                it("should be valid with correct conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "equal",
                                    "value": "2012-05-04T13:45:50",
                                    "backgroundColor": "AABBCC",
                                    "font": {
                                        "color": "FFFFFF",
                                        "underline": true,
                                        "bold": true,
                                        "italic": true
                                    }
                                },
                                {
                                    "operator": "between",
                                    "value": ["2012-05-04T13:45:50", "2014-05-04T13:45:51"],
                                    "backgroundColor": null,
                                    "font": {
                                        "color": "FF00FF",
                                        "underline": false,
                                        "bold": false,
                                        "italic": false
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": []
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "equal",
                                    "value": "2012-05-04T13:45:50",
                                    "backgroundColor": "AABBCC",
                                    "font": {
                                        "color": "FFFFFF",
                                        "underline": true,
                                        "bold": true,
                                        "italic": true
                                    }
                                },
                                {
                                    "operator": "between",
                                    "value": ["2012-05-04T13:45:50", "2014-05-04ZZZ13:45:51"],
                                    "backgroundColor": null,
                                    "font": {
                                        "color": "FF00FF",
                                        "underline": false,
                                        "bold": false,
                                        "italic": false
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });

            describe("'time' dataType", function() {
                var dataType = "time";

                it("should be valid with correct conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "equal",
                                    "value": "13:45:50",
                                    "backgroundColor": "AABBCC",
                                    "font": {
                                        "color": "FFFFFF",
                                        "underline": true,
                                        "bold": true,
                                        "italic": true
                                    }
                                },
                                {
                                    "operator": "between",
                                    "value": ["13:45:50", "13:45:51"],
                                    "backgroundColor": null,
                                    "font": {
                                        "color": "FF00FF",
                                        "underline": false,
                                        "bold": false,
                                        "italic": false
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be valid with empty conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": []
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(true);
                });

                it("should be invalid with incorrect conditions", function() {
                    var obj = {
                            "id": "test",
                            "dataType": dataType,
                            "componentType": interactiveComponentTypes.TABLE_COLUMN,
                            "conditions": [
                                {
                                    "operator": "equal",
                                    "value": "13:45:50",
                                    "backgroundColor": "AABBCC",
                                    "font": {
                                        "color": "FFFAAAAFFF",
                                        "underline": true,
                                        "bold": true,
                                        "italic": true
                                    }
                                },
                                {
                                    "operator": "between",
                                    "value": ["13:45:50", "13:45:51"],
                                    "backgroundColor": null,
                                    "font": {
                                        "color": "FF00FF",
                                        "underline": false,
                                        "bold": false,
                                        "italic": false
                                    }
                                }
                            ]
                        },
                        result = tv4.validateResult(obj, tableColumnSchema, false, true);

                    expect(result.valid).toBe(false);
                });
            });
        });
    });
});