[#ftl]
[#import "spring.ftl" as spring]
[#import "macros.ftl" as mifos]
<html>
<head>
  <title>Batch Job Runner</title>
</head>
<body>

   <input type="hidden" id="page.id" value="BatchJobRunner"/>

   [#if model.jobsExecuted?size > 0]

   Batch jobs ran:
   <ol>
       [#list model.jobsExecuted as job]
       <li>${job}*</li>
       [/#list]
   </ol>

   [#else]

   Specify batch jobs to run on as CGI query parameters in either a GET or POST
   request.
   <ul>
       <li>To run all batch jobs, pass <code>runAllBatchJobs=true</code>.</li>
       <li>Here's an example query string which runs three batch jobs:
         <code>job=GenerateMeetingsForCustomerAndSavings&amp;job=ApplyCustomerFee&amp;job=BranchReport</code>.
         Jobs will be executed in order.</li>
       <li>Omit the "Task" or "Helper" suffix of batch job names, as in the example.</li>
   </ul>

   [/#if]

[@mifos.footer /]
