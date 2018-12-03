<#assign ctx=request.contextPath />
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>收藏列表</title>
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <style>
        .pannel > div {
            width: 500px;
            text-align: center;
            margin: 10px auto;
        }
        .pannel > div {
            width: 900px;
            text-align: center;
            margin: 10px auto;
        }

        .item {
            /*height: 108px;*/
        }
        .item > div {
            display: inline-block;
        }
        .item img {
            max-height: 100px;
        }
    </style>
</head>
<body>

<div class="row">
    <div class="tools" style="text-align: right;padding-right: 50px">
        <button type="button" class="btn btn-link" onclick="rs()">切换用户</button>
    </div>
    <div class="pannel">
        <div>
            <button type="button" class="btn btn-success order" onclick="change()">随机</button>
            <button type="button" class="btn btn-success" onclick="pre()"><</button>
            <button type="button" class="btn btn-success" onclick="play()">Play/Stop</button>
            <button type="button" class="btn btn-success" onclick="next()">></button>
        </div>
        <div id="title">---</div>
        <div id="singer">---</div>
        <div>
            <img id="show" src="${ctx}/fm/img?p=${songs[0].picture}">
        </div>
    </div>
<#list songs as song>
    <div class="col-sm-4 col-md-4">
        <div class="thumbnail item" id="${song.sid}" onclick="autoPlay('${song.sid}')">
            <div>
                <#--<img src="${ctx}/fm/img?p=${song.picture}" alt="" onclick="autoPlay('${song.sid}')" class="pic">-->
            </div>
            <div class="title">
                ${song?counter}、${song.title}
            </div><br>
            <div class="singer">
                <#if song.singers??>
                    <#list song.singers as singer>${singer.name}&nbsp&nbsp|</#list>
                </#if>
            </div>
            <input class="urls" type="hidden" value="${song.url}">
            <input class="pic" type="hidden" value="${ctx}/fm/img?p=${song.picture}">
        </div>
    </div>
</#list>

    <audio id="myaudio" src="" controls="controls" loop="false" hidden="true">
    </audio>
</div>

<script>
    var curIndex = 0;
    var playing = false;
    var order = 1;
    var len = ${songs?size};

    $(function () {
        var audio = document.getElementById('myaudio');
        if(audio){
            audio.loop = false;
            audio.addEventListener('ended', function () {
                next();
            }, false);
        }
    });

    function rs() {
        location.href = '${ctx}/clear';
    }

    function play() {
        if(playing == true) {
            $("#myaudio")[0].pause();
            playing = false;
        } else {
            indexPlay(0);
        }
    }

    function change() {
        order = 1 - order;
        $(".order").html((order>0)?'随机':'顺序');
    }

    function next() {
        if(order > 0) {
            indexPlay(Math.floor(Math.random()*len));
        } else {
            indexPlay(curIndex+1);
        }
    }

    function pre() {
        if(order > 0) {
            indexPlay(Math.floor(Math.random()*len));
        } else {
            indexPlay(curIndex-1);
        }
    }

    function indexPlay(index) {
        if(index < 0)
            index = len + index;
        if(index > len-1)
            index = index % len;
        playing = true;
        var id = $(".item")[index].id;
        curIndex = index;
        autoPlay(id);
    }

    function autoPlay(id) {
        $("#myaudio").attr('src', $("#" + id + " .urls").val());
        $("#show").attr('src', $("#" + id + " .pic").val());
        $("#title").html($("#" + id + " .title").html());
        $("#singer").html($("#" + id + " .singer").html());
        $("#myaudio")[0].play();
    }
</script>

</body>
</html>