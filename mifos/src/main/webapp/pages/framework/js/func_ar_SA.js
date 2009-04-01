function arabicToAsci(char_code) {
	return convertorMap(char_code,1632,1641);
}

function FnCheckNumberOnPress_ar_SA(evt){
	return FnCheckNumberOnPressValue(evt,1632,1641);
}

function FnCheckNumber_ar_SA(event,min,max,objTextField) {
	var  value=objTextField.value;
	var asci_txt=genericConvertor(value,"arabicToAsci");
	return FnCheckNumberEnglish(event,min,max,asci_txt);
}

function FnCheckNumCharsOnPress_ar_SA(evt) {
}

function FnCheckNumChars_ar_SA(event,objTextField) {
}