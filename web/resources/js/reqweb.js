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

function progressBarHandler(event) {
    PF("confirmImpButton").disable();
    PF("cancelImpButton").disable();
    PF("stopImpButton").enable();
    PF("pbImpUsuario").start();
}

function progressBarComplete(event) {
    PF("confirmImpButton").enable();
    PF("cancelImpButton").enable();
    PF("stopImpButton").disable();
}

function progressBarCancel(event){
    PF("confirmImpButton").enable();
    PF("cancelImpButton").enable();
    PF("stopImpButton").disable();    
    PF("pbImpUsuario").cancel();    
}


