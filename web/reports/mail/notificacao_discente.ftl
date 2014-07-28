<!DOCTYPE html>
<!--
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
-->
<html>
    <head>
        <title>${TITULO}</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width">
    </head>
    <body style="padding: 8px; font-family: 'Lucida Grande', lucida, 'Helvetica CE', sans-serif!important; font-size: 10pt; color: #555; background-color: #9c9c9c;">
        <div id="header" style="background: #a8a8a8; border-bottom: thin solid #000">
            <table id="tableHeader" width="100%">
                <tr>
                    <td style="text-align: left; width: 5%"><img alt="unidade" src="cid:${logo}" /></td>
                    <td style="text-align: left; width: 95%; font-size: 26pt; color: #fff; font-weight: bold; text-indent: 8pt">${TITULO}</td>
                </tr>
            </table>
        </div>
        <div id="content" style="background: #bdccd4;">
            <table id="tableContent" width="100%">
                <tr>
                    <th style="text-align: left; width: 15%">${DISCENTE}:</th>
                    <th style="text-align: left; width: 85%">${matricula} - ${discente}</th>
                </tr>
                <tr>
                    <th style="text-align: left; width: 15%">${REQUERIMENTO}:</th>
                    <th style="text-align: left; width: 85%">${tipoRequerimento}</th>
                </tr>
                <tr>
                    <td>${STATUS}:</td>
                    <td>${status}</td>
                </tr>
                <tr>
                    <td colspan="2"><hr style="border: thin solid black;" /></td>
                </tr>
                <tr>
                    <td>${ATENDENTE}:</td>
                    <td>${atendente}</td>
                </tr>
                <tr>
                    <td colspan="2"><hr style="border: thin solid black;" /></td>
                </tr>
                <tr>
                    <td colspan="2">${OBSERVACAO}:</td>
                </tr>
                <tr>
                    <td colspan="2"><pre>${observacao}</pre></td>
                </tr>
            </table>
        </div>
    </body>
</html>
