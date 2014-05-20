/**
 * autor: Andre Luiz F. R. Barca
 * email: andrexterz@gmail.com
 */

/*************** generic functions *********************/
/**
 * 
 * @param {type} widgetVar
 * @param {type} xhr
 * @param {type} status
 * @param {type} args
 * @returns {undefined}
 */
function dialogHandler(widgetVar, xhr, status, args) {
    if (args.resultado === true) {
        PF(widgetVar).hide();
    }
    else {
        PF(widgetVar).jq.effect("highlight", {}, 800);
    }
}

function startImpDialog(widgetDialog, widgetButton, widgetProgressBar, event) {
    PF(widgetDialog).show();
    PF(widgetProgressBar).cancel();
    PF(widgetButton).jq.fadeIn();
}

function progressBarHandler(widget, event) {
    PF(widget).start();
}

function progressBarComplete(widget, event) {
    PF(widget).jq.fadeOut();
}

function progressBarCancel(widget, event) {
    PF(widget).cancel();
}

/*
 * remove todos domingos do widget calendar
 * atributo de beforShowDay
 */
function disableSundays(date) {
    var day = date.getDay();
    return eval("[(day !== 0),'']");
}