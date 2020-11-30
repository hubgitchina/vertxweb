layui.use(['form', 'jquery', 'layer'], function () {
    var form = layui.form;
    var $ = layui.jquery;
    var layer = layui.layer;

    var device = layui.device();
    var VERIFY = 'lay-verify';
    var VER_TYPE = 'lay-verType';
    var DANGER = 'layui-form-danger';
    var REQ_TEXT = 'lay-reqText';

    /**
     * 表单校验
     * @return 校验通过返回true
     */
    doVerify = function (filter) {
        var stop = false // 验证不通过状态
            , verify = form.config.verify // 验证规则
            , verifyElem = $('[name=' + filter + ']'); // 获取需要校验的元素

        // 开始校验
        layui.each(verifyElem, function (_, item) {
            var othis = $(this)
                , vers = othis.attr(VERIFY).split('|')
                , verType = othis.attr(VER_TYPE) // 提示方式
                , value = othis.val();

            othis.removeClass(DANGER); // 移除警示样式

            // 遍历元素绑定的验证规则
            layui.each(vers, function (_, thisVer) {
                var isTrue // 是否命中校验
                    , errorText = '' // 错误提示文本
                    , isFn = typeof verify[thisVer] === 'function';

                // 匹配验证规则
                if (verify[thisVer]) {
                    isTrue = isFn ? errorText = verify[thisVer](value, item) : !verify[thisVer][0].test(value);
                    errorText = errorText || verify[thisVer][1];

                    // v2.5.0 自定义 required 类型验证的提示文本
                    if (thisVer === 'required') {
                        errorText = othis.attr(REQ_TEXT) || errorText;
                    }

                    // 如果是必填项或者非空命中校验，则阻止提交，弹出提示
                    if (isTrue) {
                        //提示层风格
                        if (verType === 'tips') {
                            layer.tips(errorText, function () {
                                if (typeof othis.attr('lay-ignore') !== 'string') {
                                    if (item.tagName.toLowerCase() === 'select' || /^checkbox|radio$/.test(item.type)) {
                                        return othis.next();
                                    }
                                }
                                return othis;
                            }(), {tips: 1});
                        } else if (verType === 'alert') {
                            layer.alert(errorText, {title: '提示', shadeClose: true});
                        } else {
                            layer.msg(errorText, {icon: 5, shift: 6});
                        }

                        //非移动设备自动定位焦点
                        if (!device.android && !device.ios) {
                            setTimeout(function () {
                                item.focus();
                            }, 7);
                        }

                        othis.addClass(DANGER);
                        return stop = true;
                        // stop = true;
                        // return false;
                    }
                }
            });
            if (stop) {
                return false;
            }
        });
        return !stop;
    };
});