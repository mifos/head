/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

function fnCancel(form) {
    form.method.value = "cancel";
    form.action = "collectionsheetaction.do";
    form.submit();
}
function adjustGroupTotalForLoan(columns, size, loanProductSize, savingsProductSize) {
    if (document.getElementsByName("group[" + (size + 1) + "][" + columns + "]")[0] == undefined) {
        return;
    }
    var groupTotal = 0;
    for (i = 0; i < (size + 1); i++) {
        if (document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0] != undefined) {
            if (document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value == ".") {
                document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value = 0;
            }
            groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value);
        }
    }
    document.getElementsByName("group[" + (size + 1) + "][" + columns + "]")[0].value = groupTotal;
    setTotalsForGroup(loanProductSize, savingsProductSize, size);
}
function adjustGroupTotalForCustAcc(columns, size, loanProductSize, savingsProductSize) {
    if (document.getElementsByName("group[" + (size + 1) + "][" + columns + "]")[0] == undefined) {
        return;
    }
    var groupTotal = 0;
    for (i = 0; i < (size + 1); i++) {
        if (document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0] != undefined) {
            if (document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value == ".") {
                document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value = 0;
            }
            groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value);
        }
    }
    document.getElementsByName("group[" + (size + 1) + "][" + columns + "]")[0].value = groupTotal;
    setTotalsForGroup(loanProductSize, savingsProductSize, size);
}
function adjustGroupTotalForSav(columns, size, depwitflag, loanProductSize, savingsProductSize) {
    if (document.getElementsByName("group[" + (size + 1) + "][" + columns + "]")[0] == undefined) {
        return;
    }
    var groupTotal = 0;
    for (i = 0; i < (size + 1); i++) {
        if (depwitflag == 1) {
            if (document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0] != undefined) {
                if (document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value == ".") {
                    document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value = 0;
                }
                groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value);
            }
        }
        if (depwitflag == 2) {
            if (document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0] != undefined) {
                if (document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value == ".") {
                    document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value = 0;
                }
                groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value);
            }
        }
    }
    document.getElementsByName("group[" + (size + 1) + "][" + columns + "]")[0].value = groupTotal;
    setTotalsForGroup(loanProductSize, savingsProductSize, size);
}
function setTotalsForGroup(loanProductSize, savingsProductSize, size) {
    var dueCollection = 0;
    var loanDisb = 0;
    var withDrawals = 0;
    var otherColl = 0;
    for (i = 0; i < (loanProductSize + savingsProductSize); i++) {
        dueCollection = parseFloat(dueCollection) + parseFloat(document.getElementsByName("group[" + (size + 1) + "][" + i + "]")[0].value);
    }
    for (i = (loanProductSize + savingsProductSize); i < ((2 * loanProductSize) + savingsProductSize); i++) {
        loanDisb = parseFloat(loanDisb) + parseFloat(document.getElementsByName("group[" + (size + 1) + "][" + i + "]")[0].value);
    }
    for (i = ((2 * loanProductSize) + savingsProductSize); i < ((2 * loanProductSize) + (2 * savingsProductSize)); i++) {
        withDrawals = parseFloat(withDrawals) + parseFloat(document.getElementsByName("group[" + (size + 1) + "][" + i + "]")[0].value);
    }
    otherColl = parseFloat(document.getElementsByName("group[" + (size + 1) + "][" + ((2 * loanProductSize) + (2 * savingsProductSize)) + "]")[0].value);
    var totIssue = withDrawals + loanDisb;
    var totColl = dueCollection + otherColl;
    document.getElementsByName("dueColl")[0].value = dueCollection;
    document.getElementsByName("otherColl")[0].value = otherColl;
    document.getElementsByName("loanDisb")[0].value = loanDisb;
    document.getElementsByName("Withdrawals")[0].value = withDrawals;
    document.getElementsByName("totColl")[0].value = totColl;
    document.getElementsByName("totIssue")[0].value = totIssue;
    document.getElementsByName("netCash")[0].value = totColl - totIssue;
}
function checkTotalForCenter(rows, columns, size, initialAccNo, centerTotalCount, loanProductSize, savingsProductSize) {
    if (document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0] == undefined) {
        return;
    }
    if (document.getElementsByName("center[" + centerTotalCount + "]")[0] == undefined) {
        return;
    }
    if (document.getElementsByName("dueColl")[0] == undefined) {
        return;
    }
    if (document.getElementsByName("totColl")[0] == undefined) {
        return;
    }
    var groupTotalInitial = document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0].value;
    var centerTotalInitial = document.getElementsByName("center[" + centerTotalCount + "]")[0].value;
    var groupTotal = 0;
    for (i = initialAccNo; i < (size + initialAccNo + 1); i++) {
        if (document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0] != undefined) {
            if (document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value == ".") {
                document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value = 0;
            }
            groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("enteredAmount[" + i + "][" + columns + "]")[0].value);
        }
    }
    document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0].value = groupTotal;
    var difference = (groupTotalInitial - groupTotal);
    document.getElementsByName("center[" + centerTotalCount + "]")[0].value = (centerTotalInitial - difference);
    setTotalsForCenter(loanProductSize, savingsProductSize);
}
function adjustTotalForCenter(rows, columns, size, initialAccNo, loanProductSize, savingsProductSize, depwitflag, totalColumn, levelId) {
    if (document.getElementsByName("center[" + columns + "]")[0] == undefined) {
        return;
    }
    if (levelId == 3) {
        centerTotal = 0;
        for (i = 0; i <= rows; i++) {
            if (document.getElementsByName("group[" + i + "][" + columns + "]")[0] != undefined) {
                if (document.getElementsByName("group[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("group[" + i + "][" + columns + "]")[0].value == ".") {
                    document.getElementsByName("group[" + i + "][" + columns + "]")[0].value = 0;
                }
                centerTotal = parseFloat(centerTotal) + parseFloat(document.getElementsByName("group[" + i + "][" + columns + "]")[0].value);
            }
        }
        if (depwitflag == 1) {
            if (document.getElementsByName("depositAmountEntered[" + rows + "][" + columns + "]")[0] != undefined) {
                if (document.getElementsByName("depositAmountEntered[" + rows + "][" + columns + "]")[0].value == "" || document.getElementsByName("depositAmountEntered[" + rows + "][" + columns + "]")[0].value == ".") {
                    document.getElementsByName("depositAmountEntered[" + rows + "][" + columns + "]")[0].value = 0;
                }
                centerTotal = parseFloat(centerTotal) + parseFloat(document.getElementsByName("depositAmountEntered[" + rows + "][" + columns + "]")[0].value);
            }
        }
        if (depwitflag == 2) {
            if (document.getElementsByName("withDrawalAmountEntered[" + rows + "][" + columns + "]")[0] != undefined) {
                if (document.getElementsByName("withDrawalAmountEntered[" + rows + "][" + columns + "]")[0].value == "" || document.getElementsByName("withDrawalAmountEntered[" + rows + "][" + columns + "]")[0].value == ".") {
                    document.getElementsByName("withDrawalAmountEntered[" + rows + "][" + columns + "]")[0].value = 0;
                }
                centerTotal = parseFloat(centerTotal) + parseFloat(document.getElementsByName("withDrawalAmountEntered[" + rows + "][" + columns + "]")[0].value);
            }
        }
        document.getElementsByName("center[" + columns + "]")[0].value = centerTotal;
    } else {
        if (document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0] == undefined) {
            return;
        }
        var groupTotalInitial = document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0].value;
        var centerTotalInitial = document.getElementsByName("center[" + columns + "]")[0].value;
        var groupTotal = 0;
        for (i = initialAccNo; i < (size + initialAccNo + 1); i++) {
            if (depwitflag == 1) {
                if (document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0] != undefined) {
                    if (document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value == ".") {
                        document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value = 0;
                    }
                    groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("depositAmountEntered[" + i + "][" + columns + "]")[0].value);
                }
            }
            if (depwitflag == 2) {
                if (document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0] != undefined) {
                    if (document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value == ".") {
                        document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value = 0;
                    }
                    groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("withDrawalAmountEntered[" + i + "][" + columns + "]")[0].value);
                }
            }
        }
        document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0].value = groupTotal;
        var difference = (groupTotalInitial - groupTotal);
        document.getElementsByName("center[" + columns + "]")[0].value = (centerTotalInitial - difference);
    }
    setTotalsForCenter(loanProductSize, savingsProductSize);
}
function adjustCustAccTotalForCenter(rows, columns, size, initialAccNo, loanProductSize, savingsProductSize, levelId) {
    if (document.getElementsByName("center[" + columns + "]")[0] == undefined) {
        return;
    }
    if (levelId == 3) {
        centerTotal = 0;
        for (i = 0; i <= rows; i++) {
            if (document.getElementsByName("group[" + i + "][" + columns + "]")[0] != undefined) {
                if (document.getElementsByName("group[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("group[" + i + "][" + columns + "]")[0].value == ".") {
                    document.getElementsByName("group[" + i + "][" + columns + "]")[0].value = 0;
                }
                centerTotal = parseFloat(centerTotal) + parseFloat(document.getElementsByName("group[" + i + "][" + columns + "]")[0].value);
            }
        }
        if (document.getElementsByName("customerAccountAmountEntered[" + rows + "][" + columns + "]")[0] != undefined) {
            if (document.getElementsByName("customerAccountAmountEntered[" + rows + "][" + columns + "]")[0].value == "" || document.getElementsByName("customerAccountAmountEntered[" + rows + "][" + columns + "]")[0].value == ".") {
                document.getElementsByName("customerAccountAmountEntered[" + rows + "][" + columns + "]")[0].value = 0;
            }
            centerTotal = parseFloat(centerTotal) + parseFloat(document.getElementsByName("customerAccountAmountEntered[" + rows + "][" + columns + "]")[0].value);
        }
        document.getElementsByName("center[" + columns + "]")[0].value = centerTotal;
    } else {
        if (document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0] == undefined) {
            return;
        }
        var groupTotalInitial = document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0].value;
        var centerTotalInitial = document.getElementsByName("center[" + columns + "]")[0].value;
        var groupTotal = 0;
        for (i = initialAccNo; i < (size + initialAccNo + 1); i++) {
            if (document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0] != undefined) {
                if (document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value == "" || document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value == ".") {
                    document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value = 0;
                }
                groupTotal = parseFloat(groupTotal) + parseFloat(document.getElementsByName("customerAccountAmountEntered[" + i + "][" + columns + "]")[0].value);
            }
        }
        document.getElementsByName("group[" + (initialAccNo + size + 1) + "][" + columns + "]")[0].value = groupTotal;
        var difference = (groupTotalInitial - groupTotal);
        document.getElementsByName("center[" + columns + "]")[0].value = (centerTotalInitial - difference);
    }
    setTotalsForCenter(loanProductSize, savingsProductSize);
}
function setTotalsForCenter(loanProductSize, savingsProductSize) {
    var dueCollection = 0;
    var loanDisb = 0;
    var withDrawals = 0;
    var otherColl = 0;
    for (i = 0; i < (loanProductSize + savingsProductSize); i++) {
        dueCollection = parseFloat(dueCollection) + parseFloat(document.getElementsByName("center[" + i + "]")[0].value);
    }
    for (i = (loanProductSize + savingsProductSize); i < ((2 * loanProductSize) + savingsProductSize); i++) {
        loanDisb = parseFloat(loanDisb) + parseFloat(document.getElementsByName("center[" + i + "]")[0].value);
    }
    for (i = ((2 * loanProductSize) + savingsProductSize); i < ((2 * loanProductSize) + (2 * savingsProductSize)); i++) {
        withDrawals = parseFloat(withDrawals) + parseFloat(document.getElementsByName("center[" + i + "]")[0].value);
    }
    otherColl = parseFloat(document.getElementsByName("center[" + ((2 * loanProductSize) + (2 * savingsProductSize)) + "]")[0].value);
    var totIssue = withDrawals + loanDisb;
    var totColl = dueCollection + otherColl;
    document.getElementsByName("dueColl")[0].value = dueCollection;
    document.getElementsByName("otherColl")[0].value = otherColl;
    document.getElementsByName("loanDisb")[0].value = loanDisb;
    document.getElementsByName("WithDrawals")[0].value = withDrawals;
    document.getElementsByName("totColl")[0].value = totColl;
    document.getElementsByName("totIssue")[0].value = totIssue;
    document.getElementsByName("netCash")[0].value = totColl - totIssue;
}
function numbersonly(myfield, e) {
    var key;
    var keychar;
    if (window.event) {
        key = window.event.keyCode;
    } else {
        if (e) {
            key = e.which;
        } else {
            return true;
        }
    }
    keychar = String.fromCharCode(key);
    if ((key == null) || (key == 0) || (key == 8) || (key == 9) || (key == 13) || (key == 27) || (key == 46)) {
        return true;
    } else {
        if ((("0123456789").indexOf(keychar) > -1)) {
            return true;
        } else {
            return false;
        }
    }
}
function makeRegEx(fmt, txt) {
    whole_part_length = "";
    fraction_part_length = "";
    if (fmt == null) {
        fmt = txt;
    } else {
        fmt = fmt.toString();
        var index = fmt.indexOf(".");
        if (-1 == index) {
            if (fmt < 1) {
                whole_part_length = "";
                fraction_part_length = "";
            } else {
                whole_part_length = fmt;
                fraction_part_length = "";
            }
        } else {
            if (fmt.substring(0, index) < 1) {
                whole_part_length = "";
            } else {
                whole_part_length = fmt.substring(0, index);
            }
            if (fmt.substring(index + 1, fmt.length) < 1) {
                fraction_part_length = "";
            } else {
                fraction_part_length = fmt.substring(index + 1, fmt.length);
            }
        }
    }
    if ((whole_part_length == "") && (fraction_part_length == "")) {
        pattern = new RegExp("^[0-9]*[.]{0,1}[0-9]*$");
    } else {
        if ((whole_part_length == "") && (fraction_part_length != "")) {
            pattern = new RegExp("[.][0-9]{1," + fraction_part_length + "}$");
        } else {
            if ((whole_part_length != "") && (fraction_part_length == "")) {
                pattern = new RegExp("^[0-9]{1," + whole_part_length + "}$");
            } else {
                pattern = new RegExp("^[0-9]{1," + whole_part_length + "}[.][0-9]{1," + fraction_part_length + "}$");
            }
        }
    }
}
function doValidation(element, fmt) {
    return true;
    /*  
	var txt = element.value;
    if (txt == "") {
        return true;
    }
    makeRegEx(fmt, txt);
    try {
        return eval(validate(txt));
    }
    catch (e) {
    }
*/
}
function validate(txt) {
    if (true == pattern.test(txt.toString())) {
    } else {
        var format = "";
        for (i = 0; i < whole_part_length; i++) {
            format += "X";
        }
        if (fraction_part_length > 1) {
            format += ".";
            for (i = 0; i < fraction_part_length; i++) {
                format += "X";
            }
        }
        eval(Alertformat(format));
        return false;
    }
}
function Alertformat(format) {
    alert("Please enter a valid format  " + format);
}

