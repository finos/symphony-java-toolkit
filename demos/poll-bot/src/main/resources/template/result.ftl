  <p><mention uid="${entity.workflow_001.poller.id}" /> Asked: </p>
  <h4>${entity.workflow_001.question}</h4>  
   <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

  <table>
    <tr>
        <th>Answer</th>
        <th style="text-align:right">Votes</th>
        <th></th>
    </tr>
    <#list entity['workflow_001'].options as o>
        <tr>
            <td>${o?index + 1}: ${o}</td>
            <td style="text-align:right">${entity['workflow_001'].counts[o?index]}</td>
            <td><div style='background-color:#29b6f6;width:${entity['workflow_001'].counts[o?index] * 30}px'> </div></td>
        </tr>
    </#list>
</table>
<div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

<p>Total Responses:<b><span class="tempo-text-color--theme-accent"> ${entity.workflow_001.totalResponses!''} </span></b>  <hash tag="${entity.workflow_001.pollID.name!''}" /></p> 