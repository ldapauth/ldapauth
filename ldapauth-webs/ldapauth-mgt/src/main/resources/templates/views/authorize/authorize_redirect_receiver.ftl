<!DOCTYPE html>
<html >
<head>
    <title>Redirect To Receiver</title>
        <script>
        <#if 'flutter'==model.receiver>
            setTimeout(() => {
                Toaster.postMessage('${model.code}||${model.state}');
            }, 1000);
       </#if>  
       </script>
</head>
<body>
</body>
</html>
