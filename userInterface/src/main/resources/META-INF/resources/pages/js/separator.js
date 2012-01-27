function isNumber(n) {
	return !isNaN(parseFloat(n)) && isFinite(n);
}

function parseFloatOpts(num, decimal, thousands) {
    var bits = num.split(decimal, 2),
    ones = bits[0].replace(new RegExp('\\' + thousands, 'g'), '');
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

String.prototype.count=function(s1) {
    return (this.split(s1).length - 1);
}

function validateNumber(num, decimalSeparator, groupSeparator, groupSize) {
	if (num.count(decimalSeparator) > 1) {
		return false;
	}

	var groupCount = 0;
	var index = (num.count(decimalSeparator) > 0) ? num.indexOf(decimalSeparator)-1 : num.length-1;
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
	
    $(".submit").click(function(event) {
    	$(".separatedNumber").each(function(index){
    		var amount;
    		var input = $(this).val();
    		if (validateNumber(input, decimalSeparator, groupingSeparator, groupingSize) == false) {
    			amount = NaN;
    		} 
    		else {
    			amount = parseFloatOpts(input, decimalSeparator, groupingSeparator);
    		}
    		if (!isNaN(amount)) {
    			$(this).val(amount);
    		}
    	});
    });
});