!function(e,n){"function"==typeof define&&define.amd?define(["../numeral"],n):n("object"==typeof module&&module.exports?require("../numeral"):e.numeral)}(this,function(e){e.register("locale","nl-nl",{delimiters:{thousands:".",decimal:","},abbreviations:{thousand:"k",million:"mln",billion:"mrd",trillion:"bln"},ordinal:function(e){var n=e%100;return 0!==e&&n<=1||8===n||n>=20?"ste":"de"},currency:{symbol:"€ "}})});