<%--
  Created by IntelliJ IDEA.
  User: Decent
  Date: 6/27/2017
  Time: 3:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <script type="text/javascript" src="../jquery.min.js"></script>
    <script type="text/javascript" src="../js/jquery.form.js"></script>
    <script type="text/javascript">
        $(function () {
            //1.用户选择图片文件后将其显示在页面
            $("#file").change(function () {
                readAsDataURL();
            })

            //2.以ajaxForm提交表单保存图片至服务器
            var options = {
                target:        '#showResponse',   // target element(s) to be updated with server response
//                beforeSubmit:  showRequest,  // pre-submit callback
//                success:       showResponse,  // post-submit callback
                // other available options:
                url:       "/TestController/uploadImg.do",         // override for form's 'action' attribute
                type:      'post'    ,    // 'get' or 'post', override for form's 'method' attribute
                dataType: 'json',        // 'xml', 'script', or 'json' (expected server response type)
                //clearForm: true        // clear all form fields after successful submit
                //resetForm: true        // reset the form after successful submit
                // $.ajax options can be used here too, for example:
                timeout:   3000,
                beforeSend: function() {
                },
                uploadProgress: function(event, position, total, percentComplete) {
                },
                success: function(responseText, statusText, xhr, $form) {
                    if(responseText.success){
                        //服务器成功存储图片后返回图片在服务器上存储的地址在页面将其保存一下然后页面
                        //可以用这个地址去服务器上请求这个图片资源
                        $("#filePath").val(responseText.message);
                        return ;
                    }
                    alert(responseText.message);
                },
                complete: function(xhr) {
                }
            };
            // bind form using 'ajaxForm
            $('#formsub').ajaxForm(options);


            //3.向服务器请求图片，服务器返回文件流的形式,页面处理后展示
            $("#read").click(function () {
                var xhr=new XMLHttpRequest();
                xhr.onload=function () {
                    var objectUrl=URL.createObjectURL(this.response);
                    var  img=document.createElement("img");
                    img.src=objectUrl;
                    img.onload=function (e) {
                        window.URL.revokeObjectURL(this.src)
                    }
                    if(img.src){
                        $("#result").html(img);
                    }
                }
                xhr.open("post","../TestController/queryPicture.do")
                xhr.responseType='blob'
                var formData = new FormData();
                //发送给服务器一个图片的地址来请求资源
                formData.append('filePath', $("#filePath").val());
                xhr.send(formData)
            })

        });
        //显示图片
        function readAsDataURL(){
            //检验是否为图像文件
            var file = document.getElementById("file").files[0];
            if(!/image\/\w+/.test(file.type)){
                alert("请选择图片文件");
                return false;
            }
            var reader = new FileReader();
            //将文件以Data URL形式读入页面
            reader.readAsDataURL(file);
            reader.onload=function(e){
                var result=document.getElementById("result");
                //显示文件
                result.innerHTML='<img src="' + this.result +'" alt="" />';
            }
        }
    </script>
    <title>Title</title>
</head>
<body>
<form id="formsub"  method="post" enctype="multipart/form-data">
    图片:<input type="file" name="imgFile" id="file">
    <input type="submit" value="提交">
    <input type="button" id="read" value="查询图片">
    <div id="result"></div>
</form>
<div>
    <input  type="text"    id="filePath">
</div>
</body>
</html>
