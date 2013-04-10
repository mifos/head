function syncAdminDocumentLinkWithComboBox(selectClassName, aElementClass) {

    $("select." + selectClassName).change(function() {
        var aElement = $(this).next("a." + aElementClass);
        var url = aElement.attr("href");
        var paramNameStr = "outputTypeId=";
        var outputTypeParamIndex = url.indexOf(paramNameStr) + paramNameStr.length;
        var outputTypeParamOldValue = url.substr(outputTypeParamIndex, outputTypeParamIndex + 1);
        var outputTypeParamNewValue = $(this).val();
        url = url.replace(paramNameStr + outputTypeParamOldValue, paramNameStr + outputTypeParamNewValue);
        
        aElement.attr("href", url);
        
        // HTML, as seen in PentahoOutputType.java
        if (outputTypeParamNewValue == 3) {
            aElement.attr("target", "_blank");
        }
        else {
            aElement.removeAttr("target");
        }
    });

}
