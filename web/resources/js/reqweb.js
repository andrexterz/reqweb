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
        PF(widgetVar).jq.effect("highlight",{},800);
    }
}

function handleImportDialog(widget, event) {
        PF("confirmImpButton").jq.show();
        PF(widget).show();
}

function progressBarHandler(widget, event) {
    PF("confirmImpButton").disable();
    PF("cancelImpButton").disable();
    PF(widget).start();
}

function progressBarComplete(event) {
    PF("confirmImpButton").enable();
    PF("cancelImpButton").enable();
    PF("confirmImpButton").jq.hide();
}

function progressBarCancel(widget, event){
    PF("confirmImpButton").enable();
    PF("cancelImpButton").enable();
    PF(widget).cancel();
}


