[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]

<head>
<title>preview fee</title>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
<link type="text/css" rel="stylesheet" href="pages/framework/css/cssstyle.css">
</head>

<body>
<form action="/mifos/defineFee.ftl" method="post" name="feeactionform">
this is the preview fee form
<input type="submit" name="_eventId_proceed" value="proceed" />
<input type="submit" name="_eventId_cancel" value="cancel" />
<input type="submit" name="xyz" value="xyz" />
</form>

</body>