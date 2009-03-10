[#ftl]
[#import "spring.ftl" as spring]

[@mifos.header "title" /]
    <h1>[@spring.message "welcomeToMifos" /]</h1>
    <p>Chinese text: [@spring.message "chineseText" /]</p>
    <p>Arabic text: [@spring.message "arabicText" /] </p>
    <p>Hindi text: [@spring.message "hindiText" /] </p>
    <p>French text: [@spring.message "frenchText" /] </p>
    [#include "footer.ftl"]    
[@mifos.footer /]

