[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

<head>
<title>define fee</title>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
<link type="text/css" rel="stylesheet" href="pages/framework/css/cssstyle.css">
</head>

<body>
<form action="defineFee.ftl?execution=${flowExecutionKey}" method="post" name="feeactionform">
Define new fee <br/>
<input type="submit" name="_eventId_preview" value="preview" /> <br/>
<input type="submit" name="_eventId_cancel" value="cancel" /> <br/>
<input type="submit" name="xyz" value="xyz" /> <br/>
<a href="${flowExecutionUrl}&_eventId=cancel">Cancel</a> <br/>
</form>

</body>