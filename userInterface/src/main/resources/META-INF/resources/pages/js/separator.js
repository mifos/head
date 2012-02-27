function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

String.prototype.count=function(s1) {
    return (this.split(s1).length - 1);
}


String.prototype.insert=function(s2, index) {
    return this.substr(0, index) + s2 + this.substr(index);
}

function formatNumber(number, decimalSeparator, groupSeparator, groupSize) {
	var result = number;
	var separatorIndex = result.indexOf('.');
	if (separatorIndex < 0) {
		separatorIndex = result.length;
	}
	
	result = result.replace('.', decimalSeparator);
	/*for (i = separatorIndex-3; i > 0; i -= groupSize) {
		result = result.insert(groupSeparator, i)
	}*/
	
	return result;
}

function parseFloatOpts(num, decimal, thousands) {
	
	//is thousand separator a space
	var testSpace = /^\s+$/.test(thousands);
	
	var bits = num.split(decimal, 2);
	var ones;
	
	if(testSpace == true) {
		ones = bits[0].split(' ').join('');
	}
	else {
		ones = bits[0].replace(new RegExp('\\' + thousands, 'g'), "");
	}
	
    if (bits[1] == undefined) {
    	bits[1] = 0;
    }
    if (isNumber(ones) && isNumber(bits[1])) {
    	ones = parseFloat(ones),
        decimal = parseFloat('0.' + bits[1]);
    	return ones + decimal;
    }
    else {
    	return NaN;
    }
}


function validateNumber(num, decimalSeparator, groupSeparator, groupSize) {
	if (num.count(decimalSeparator) > 1) {
		return false;
	}

	if (num.indexOf(decimalSeparator) != -1 && num.indexOf(groupSeparator) != -1) {
		if (num.lastIndexOf(groupSeparator) > num.indexOf(decimalSeparator)) {
			return false;
		}
	}
	
	var groupCount = 0;
	var index = (num.count(decimalSeparator) > 0) ? num.indexOf(decimalSeparator)-1 : num.length-1;
	
	var testSpace = /^\s+$/.test(groupSeparator);
	if (testSpace) {
		groupSeparator = "$";
		num = num.split(' ').join('$');
	}
	
	//grouping
	for (var i = index; i >= 0; i--) {
		if (num.charAt(i) == groupSeparator) {
			if (groupCount != groupSize) {
				return false;
			}
			else {
				groupCount = 0;
			}
		}
		else {
			groupCount++;
		}
	}
	
	//last group
	if (num.count(groupSeparator) > 0 && groupCount > groupSize) {
		return false;
	}
	
	return true;
}

$(document).ready(function() {
	
	var decimalSeparator = $("#format\\.decimalSeparator").attr("title");
	var groupingSeparator = $("#format\\.groupingSeparator").attr("title");
	var groupingSize = $("#format\\.groupingSize").attr("title");
	
	if (decimalSeparator == undefined || groupingSeparator == undefined || groupingSize == undefined) {
		return;
	}
	
	$(".separatedNumber").each(function(index) {
		var input = $(this).val();
		if (isNumber(input)) {
			var formattedNumber = formatNumber(input, decimalSeparator, groupingSeparator, groupingSize);
			$(this).val(formattedNumber);
		}
	});
	
	
    $(".submit").click(function(event) {
    	$(".separatedNumber").each(function(index){
    		var amount;
    		var input = $(this).val();
    		var digitsAfterDecimal = (input.indexOf(decimalSeparator) == -1) ? 0 : input.length-1 - input.indexOf(decimalSeparator); 
    		
    		if (validateNumber(input, decimalSeparator, groupingSeparator, groupingSize) == false) {
    			amount = NaN;
    		} 
    		else {
    			amount = parseFloatOpts(input, decimalSeparator, groupingSeparator);
    		}
    		if (!isNaN(amount)) {
    			var output = (digitsAfterDecimal > 0) ? amount.toFixed(digitsAfterDecimal) : amount;
    			$(this).val(output);
    		}
    	});
    });
});