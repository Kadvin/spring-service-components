/**
 * Created by user on 2014/11/25.
 */

function showAdd(dict){
    var detailItems = dict.details;
    for(var i = 1;i<detailItems.length;i++) {
        var detail = detailItems[i];
        add(detail);
    }
}
var editCount = 1;
function add(detail) {
    editCount++;
    //自增id不同的HTML对象，并附加到容器最后

    var elementDiv = document.createElement("div");
    elementDiv.setAttribute("id","create"+editCount);
    elementDiv.setAttribute("class","form-group has-info");
    document.getElementById("editcontent").appendChild(elementDiv);

    var elementLabel = document.createElement("label");
    var textLable = document.createTextNode("名称");
    elementLabel.setAttribute("for","");
    elementLabel.setAttribute("class","col-xs-12 col-sm-3 control-label no-padding-right");
    elementLabel.appendChild(textLable);

    var elementDiv2 = document.createElement("div");
    var elementInput = document.createElement("input");
    elementDiv2.setAttribute("class","col-xs-12 col-sm-2");
    elementInput.setAttribute("type","text");
    elementInput.setAttribute("name","keyInput");
    elementInput.setAttribute("value",detail?detail.key:'');
    elementDiv2.appendChild(elementInput);
    //-----------------------------------------------
    var elementLabel2 = document.createElement("label");
    var textLable2 = document.createTextNode("值");
    elementLabel2.setAttribute("for","");
    elementLabel2.setAttribute("class","col-xs-12 col-sm-1 control-label no-padding-right");
    elementLabel2.appendChild(textLable2);


    var elementDiv3 = document.createElement("div");
    elementDiv3.setAttribute("class","col-xs-12 col-sm-2");
    var elementInput2 = document.createElement("input");
    elementInput2.setAttribute("type","text");
    elementInput2.setAttribute("name","valueInput");
    elementInput2.setAttribute("value",detail?detail.value:'');
    elementDiv3.appendChild(elementInput2);

    var elementButton = document.createElement("button");
    elementButton.setAttribute("onclick","del("+editCount+")");
    elementButton.setAttribute("id",editCount+"");
    var textA = document.createTextNode("删除");
    elementButton.appendChild(textA);

    elementDiv.appendChild(elementLabel);
    elementDiv.appendChild(elementDiv2);
    elementDiv.appendChild(elementLabel2);
    elementDiv.appendChild(elementDiv3);
    elementDiv.appendChild(elementButton);
}
//删除指定元素
function del(editCount){
    var elementB = document.getElementById(editCount);
    document.getElementById('editcontent').removeChild(elementB.parentNode);
}

