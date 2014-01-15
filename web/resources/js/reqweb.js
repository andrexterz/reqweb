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

function handleImportDialog(event) {
        PF("confirmImpButton").jq.show();
        PF("importaUsuarioLDAPDialog").show();
}

function progressBarHandler(event) {
    PF("confirmImpButton").disable();
    PF("cancelImpButton").disable();
    PF("pbImpUsuario").start();
}

function progressBarComplete(event) {
    PF("confirmImpButton").enable();
    PF("cancelImpButton").enable();
    PF("confirmImpButton").jq.hide();
}

function progressBarCancel(event){
    PF("confirmImpButton").enable();
    PF("cancelImpButton").enable();
    PF("pbImpUsuario").cancel();    
}


