[#ftl]
[#import "spring.ftl" as spring]

[@mifos.header "title" /]
    <h1>[@spring.message "welcomeToMifos" /]</h1>
    <p>[@spring.message "englishChineseText"/]: [@spring.message "chineseText" /]</p>
    <p>[@spring.message "englishArabicText"/]: [@spring.message "arabicText" /] </p>
    <p>[@spring.message "englishHindiText"/]: [@spring.message "hindiText" /] </p>
    <p>[@spring.message "englishFrenchText"/]: [@spring.message "frenchText" /] </p>
    [#include "footer.ftl"]    
[@mifos.footer /]

