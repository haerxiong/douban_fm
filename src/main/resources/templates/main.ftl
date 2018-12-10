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
        <button type="button" class="btn btn-link" onclick="goUrl('${ctx}/clear')">切换用户</button>
        <button type="button" class="btn btn-link" onclick="goUrl('${ctx}/fm/dowload')">下载歌单</button>
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
            喜爱度：
            <label class="radio-inline">
                <input type="radio" value="0" name="repeat" ${(songs[0].repeat=="0")?string("checked", "")}>普通
            </label>
            <label class="radio-inline">
                <input type="radio" value="1" name="repeat" ${(songs[0].repeat=="1")?string("checked", "")}>大概率随机
            </label>
            <label class="radio-inline">
                <input id="more" type="checkbox" onclick="repeat=(this.checked)?1:0;">多放一次
            </label>
        </div>
        <div>
            <img id="show" src="${ctx}/fm/img?p=${songs[0].picture}">
        </div>
    </div>
<#list songs as song>
    <div class="col-sm-4 col-md-4">
        <div class="thumbnail item" id="${song.sid}" onclick="curIndex=${song?index};autoPlay('${song.sid}')">
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
            <input class="repeat" type="hidden" value="${song.repeat!0}" index="${song?index}">
            <input class="pic" type="hidden" value="${ctx}/fm/img?p=${song.picture}">
        </div>
    </div>
</#list>

    <audio id="myaudio" src="" controls="controls" loop="false" hidden="true">
    </audio>
</div>

<script>
    var curIndex = 0;
    var order = 1;
    var len = ${songs?size};
    var repeat = 0;

    $(function () {
        var audio = document.getElementById('myaudio');
        if(audio){
            audio.loop = false;
            audio.addEventListener('ended', function () {
                next();
            }, false);
        }
    });

    $('[type="radio"]').change(function(e){
        var val = e.target.value;
        var id = $(".item")[curIndex].id;
        $.ajax({
            type: 'POST',
            url: "${ctx}/fm/repeat/"+id,
            data: {repeat:val},
            dataType: "json",
            success: function(e) {
                $("#" + id + " .repeat").val(val);
            }
        });
    });

    function goUrl(url) {
        location.href = url;
    }

    function play() {
        if($("#myaudio")[0].paused) {
            $("#myaudio")[0].play();
        } else if($("#myaudio")[0].played) {
            $("#myaudio")[0].pause();
        } else {
            indexPlay(0);
        }
    }

    function change() {
        order = 1 - order;
        $(".order").html((order>0)?'随机':'顺序');
    }

    function next() {
        if(repeat == 1) {
            indexPlay(curIndex);
            repeat = 0;
            $("#more").prop("checked", false);
            return;
        }
        if(order > 0) {
            // 高频歌曲，随机时多N次被选中机会
            // 歌曲出现的概率为R, 红心歌曲数量L, 高频歌曲数量X
            // 放大倍数N = (1/1-R)(RL/x-R)
            var R = 1/5;
            var L = len;
            var X = $(".repeat[value='1']").length;
            var N = Math.round((1/(1-R))*(R*L/X-R));
            var rand = Math.floor(Math.random()*(len+X*N));
            alert(rand);
            if(rand > L-1 && N > 0) {
                t = (rand - L) % X;
                var rand = $($(".repeat[value='1']")[t]).attr("index");
            }
            indexPlay(rand);
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
        var rp = $("#" + id + " .repeat").val() + "";
        $(":radio[name='repeat']").prop("checked", false);
        $(":radio[name='repeat'][value='" + rp + "']").prop("checked", true);
    }
</script>

</body>
</html>