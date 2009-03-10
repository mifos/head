/* Cheetah CSS */

#clientsAndAccountsPage {
   margin: 0 0 0 200px;
}

#loanDetailPage {
   margin: 0 0 0 200px;
}

#clientDetailPage-loanList {
   margin: 0 0 0 20px;
}

#clientDetailPage-loanSectionHeading {
   margin: 0 0 0 10px;
}

#clientDetailPage-loanSection {
   background-color: #d7deee;
   width: 400px;
   height: 200px;
   margin: 0 0 0 0px;   
}

#clientDetailPage {
   margin: 0 0 0 200px;
}

#loanProductName {
   text-align: left;
}

#loanAmount {
   text-align: left;
}

#clientName {
   text-align: left;
}

.fontnormalboldorangeheading {

   text-decoration:none; 
   color:#CC6601;
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 11pt;
   font-weight: bold;
}
#homePageHeader { 
   border-bottom: 6px #FF9600 solid; 
}

#standardPageContent {
   margin:0 0 0 200px;
}
   
#homePageContent {
   margin:0 0 0 200px;
}

#adminPageContent {
   margin:0 0 0 200px;
}

#logo { 
   float:left; 
}

#top-menu-bar { 
   padding:0px 0px 0 0px;
   margin:0px 0 0px 0;
}

ul#simple-menu {
   list-style-type:none;
   position:relative;
   height:63px;
   font-family:Arial,Verdana, Helvetica, sans-serif;
   font-size:9pt;
   font-weight:bold;margin:0px 0 0 0;
   padding:11px 0 0 0;
}

ul#simple-menu li {
      display:block;
      float:left;
      margin:34px 0 0 2px;
      height:27px;
   }

ul#simple-menu li.left {
   margin:0; 
}

ul#simple-menu li a {
   display:block;
   float:left;
   color:#000166;background:#F2D1A6;
   line-height:27px;
   text-decoration:none;
   padding:0 17px 0 18px;
   height:27px;
}
ul#simple-menu li a.right {
   padding-right:19px;
}

ul#simple-menu li a:hover{
   background:#FF9600;
}

ul#simple-menu li a.current{
   color:#000166;
   background:#FF9600;
}

ul#simple-menu li a.current:hover {
   color:#000166;
   background:#FF9600;
}

#top-right-links {
   width: 100%;
   text-align: right;
}

a#settings {
   height:20px;
   margin:0 0 0 0;
   font-family: Arial, Verdana, Helvetica, sans-serif;  
   font-size: 9pt;
}

a#logout {
   height:20px;
   margin:8px 18px 0 0;
   font-family: Arial, Verdana, Helvetica, sans-serif;  
   font-size: 9pt;
}

p.boldOrange {
   text-decoration:none;  
   color:#CC6601;
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 9pt;
   font-weight: bold;   
}
   
div#page-content {
   padding-top: 15px;
   min-height: 480px;
   background-color: white;
   padding-left: 5px;
   padding-right: 5px;
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 9pt;
   margin: 0 0 0 10px;
}
   
div#page-content h2 {
   color: #CC6601;
   font-size: 11pt;
   font-weight: bold;
}

div#page-content h3 {
   color: #CC6601;
   font-size: 11pt;
   font-weight: bold;
}

div#page-content h4 {
   color: black;
   font-size: small;
   font-weight: bold;
}

div#page-content table {
      font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 9pt;
}

#login {
   background-color: white;
   margin:auto;
   width: 500px;
   border:1px #d7deee solid;
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: small;
   color: black;
   overflow: hidden; /* a trick to get the div to stretch to accommodate the floats inside */
}

#login-header {
   text-align: left;
   background-color: #d7deee;
   font-size: larger;
   font-weight: bold;
   padding: 2px 10px 2px 10px;
}

#login-welcome {
   text-align: left;
   width: 30%;
   float: left;
   padding-left: 10px;
   padding-top: 10px;
   height: auto;
}

#login-interaction {
   width: 60%;
   float: right;
   overflow: hidden;
   border-left:1px #d7deee solid;
}

#login-table {
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: small;
   min-width: 250px;
   border-left:1px #d7deee solid;
   padding: 0px 10px 10px 10px;
}

table#login-table td.label {
   text-align: right;
}

table#login-table td#login-submit {
   padding-top: 20px;
}

#login-page-header {
   background-attachment: scroll;
   background-image: url(../images/logo.gif);
   background-repeat: no-repeat;
   background-position: left top;
   background-color: white;
   height: 75px;
   width: 100%;
   border-bottom: 6px orange solid;
}

.error-messages {
   color: red;
   font-weight: bold;
   padding: 10px 10px 0px 10px;
}

/* default styles for forms */

form {  /* set width in form, not fieldset (still takes up more room w/ fieldset width */
  margin: 0;
  padding: 0;

}

fieldset {  /* Setting border-color for the tag alone does not work in Firefox */
   border-color: #d7deee;
   border-width: 2px;
   border-style: solid;
   padding: 10px;
   margin: 0;
   width: 600px;
}

fieldset legend {
   font-size:10pt; /* bump up legend font size, not too large or it'll overwrite border on left */
                       /* be careful with padding, it'll shift the nice offset on top of border  */
}

fieldset label {
   display: block;  /* block float the labels to left column, set a width */
   float: left;
   padding: 0;
   margin: 5px 0 0; /* set top margin same as form input - textarea etc. elements */
   text-align: right;
   width: 300px;
}

fieldset input, form textarea, form select {
   /* display: inline; inline display must not be set or will hide submit buttons in IE 5x mac */
   width:auto;      /* set width of form elements to auto-size, otherwise watch for wrap on resize */
   margin:5px 0 0 10px; /* set margin on left of form elements rather than right of
                              label aligns textarea better in IE */
}

fieldset fieldset label:first-letter { /* use first-letter pseudo-class to underline accesskey, note that */
   text-decoration:underline;    /* Firefox 1.07 WIN and Explorer 5.2 Mac don't support first-letter */
                                    /* pseudo-class on legend elements, but do support it on label elements */
                                    /* we instead underline first letter on each label element and accesskey */
                                    /* each input. doing only legends would  lessens cognitive load */
                                   /* opera breaks after first letter underlined legends but not labels */
}

form small {
   display: block;
   margin: 0 0 5px 160px; /* instructions/comments left margin set to align w/ right column inputs */
   padding: 1px 3px;
   font-size: 88%;
}

fieldset .required{ /* uses class instead of div, more efficient */
   font-weight:bold;
}

/* form styles for login */

form#login {
  min-width: 400px;
  max-width: 600px;
  width: 450px;
}

/* form loan product overrides */

form.loan-product {
   width: 600px;
}

form.loan-product fieldset label{
   width: 300px;
}

/* Left pane */

.left-pane-heading-group {
   margin:0 0 0 10px;
}

.left-pane {
   margin-top: 2px;
   float: left;
   height: 100%;
   background:#F2D1A6;
   width: 180px;
}

.left-pane-top-spacer {
   height: 3px;
   background-color: #FFFFFF;
}

.left-pane-header {
   padding-top: 2px;
   padding-bottom: 2px;
   padding-left: 4px;
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 10pt;
   font-weight: bold;
   color:#FFFFFF;
   background-color:#FF9600;
   }

.left-pane-content {
   padding-left: 5px;
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 9pt;
}

.left-pane-content h2 {
   padding-left: 5px;
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 9pt;
}

.left-pane-content a {
   font-weight: normal;
   font-size: 9pt;  
}

.content-pane {
   margin-top: 5px;
   margin-left: 5px; 
   width:800px;  
}

.content-pane h2 {
   color: red;
}

/* create client page */
.createClient form { 
  font-family:verdana,arial,sans-serif;
  margin: 0;
  padding: 0;
}

form fieldset.noborder {
  border-width: 1px;
  border-style: none;
  padding: 10px;
  margin: 0;
}

form fieldset.createClient legend {
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 12pt;
}

form fieldset.createClient label {
   font-family: Arial, Verdana, Helvetica, sans-serif;
   font-size: 11pt;
}

/* Display a list on one line, each entry separated by "|" */
.navigation-list
{
list-style: none;
padding: 0;
margin: 0 0 0 10pt;
}

.navigation-list li
{
display: inline;
padding: 0;
margin: 0;
}

.navigation-list li:before { content: "| "; }
.navigation-list li:first-child:before { content: ""; }
}

