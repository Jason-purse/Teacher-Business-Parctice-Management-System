(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-e0795a80"],{"4ede":function(e,r,t){},"9ed6":function(e,r,t){"use strict";t.r(r);var s=function(){var e=this,r=e.$createElement,t=e._self._c||r;return t("div",{staticClass:"login-page"},[t("div",{staticClass:"login-container"},[e._m(0),t("div",{staticClass:"login-form"},[t("div",{staticClass:"tab-box"},e._l(e.tabList,(function(r,s){return t("div",{key:s,staticClass:"tab-item cu",class:e.tabValue===r.value?"active":"",on:{click:function(t){return e.testTabClick(r.value)}}},[e._v(" "+e._s(r.name)+" ")])})),0),t("div",{staticClass:"login"},[t("el-form",{ref:"loginForm",attrs:{model:"account"===e.tabValue?e.loginForm:e.registerForm,rules:e.loginRules}},["account"===e.tabValue?[t("el-form-item",{attrs:{prop:"username"}},[t("el-input",{attrs:{"prefix-icon":"el-icon-user",placeholder:"账号",clearable:""},model:{value:e.loginForm.username,callback:function(r){e.$set(e.loginForm,"username",r)},expression:"loginForm.username"}})],1),t("el-form-item",{attrs:{prop:"password"}},[t("el-input",{attrs:{"show-password":"",type:"password","prefix-icon":"el-icon-lock",placeholder:"密码",clearable:""},model:{value:e.loginForm.password,callback:function(r){e.$set(e.loginForm,"password",r)},expression:"loginForm.password"}})],1)]:[t("el-form-item",{attrs:{prop:"email"}},[t("el-input",{attrs:{"prefix-icon":"el-icon-user",placeholder:"邮箱",clearable:""},model:{value:e.registerForm.email,callback:function(r){e.$set(e.registerForm,"email",r)},expression:"registerForm.email"}})],1),t("el-form-item",{attrs:{prop:"username"}},[t("el-input",{attrs:{placeholder:"姓名",clearable:""},model:{value:e.registerForm.username,callback:function(r){e.$set(e.registerForm,"username",r)},expression:"registerForm.username"}})],1),t("el-form-item",{attrs:{prop:"nickname"}},[t("el-input",{attrs:{placeholder:"昵称",clearable:""},model:{value:e.registerForm.nickname,callback:function(r){e.$set(e.registerForm,"nickname",r)},expression:"registerForm.nickname"}})],1),t("el-form-item",{attrs:{prop:"password"}},[t("el-input",{attrs:{"show-password":"",type:"password","prefix-icon":"el-icon-lock",placeholder:"密码",clearable:""},model:{value:e.registerForm.password,callback:function(r){e.$set(e.registerForm,"password",r)},expression:"registerForm.password"}})],1)]],2),t("div",{staticClass:"tips"},[e._v(" 密码为8-16位大小写字母、数字至少两种组合，不可包含空格、中文，特殊符号等字符 ")]),"account"===e.tabValue?t("el-button",{staticStyle:{width:"100%"},attrs:{type:"primary",size:"small"},on:{click:e.handleLogin}},[e._v("登 录")]):t("el-button",{staticStyle:{width:"100%"},attrs:{type:"primary",size:"small"},on:{click:e.handleRegister}},[e._v("注 册")])],1)])])])},a=[function(){var e=this,r=e.$createElement,s=e._self._c||r;return s("div",{staticClass:"login-pic"},[s("img",{attrs:{src:t("fe63")}}),s("div",{staticClass:"title"},[e._v("教师企业实践管理")])])}],i=t("5530"),o=(t("ac1f"),t("00b4"),t("d3b7"),t("d81d"),t("4de4"),t("caad"),t("2532"),t("c24f")),n=t("2f62"),l=t("5f87"),c={name:"Login",data:function(){var e=function(e,r,t){var s=/^\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/;s.test(r)?t():t(new Error("邮箱格式错误, 请重新输入"))};return{loginForm:{username:"test",password:"123456"},registerForm:{username:"",nickname:"",email:"",password:""},loginRules:{username:[{required:!0,trigger:"blur",message:"请输入用户账号"}],password:[{required:!0,trigger:"blur",message:"请输入用户密码"}],nickname:[{required:!0,trigger:"blur",message:"请输入用户昵称"}],email:[{required:!0,trigger:"blur",validator:e}]},loading:!1,passwordType:"password",redirect:void 0,tabValue:"account",tabList:[{name:"账号密码",value:"account"},{name:"注册",value:"register"}]}},watch:{},methods:Object(i["a"])(Object(i["a"])(Object(i["a"])({},o["a"].methods),Object(n["c"])("user",["SET_USERINFO"])),{},{testTabClick:function(e){this.tabValue=e,this.$refs.loginForm.clearValidate()},showPwd:function(){var e=this;"password"===this.passwordType?this.passwordType="":this.passwordType="password",this.$nextTick((function(){e.$refs.password.focus()}))},handleLogin:function(){var e=this;this.$refs.loginForm.validate((function(r){if(!r)return console.log("error submit!!"),!1;e.loading=!0,e.$store.dispatch("user/login",e.loginForm).then((function(){var r=e.$message.success("登陆成功 !!!");sessionStorage.setItem("isFirst",1),e.loading=!1,e.getCurrentUserInfo().then((function(r){var t=r.result;e.userInfo=t,e.formInfo=Object(i["a"])({},t),Object(l["h"])((t.roles||[]).map((function(e){return e.itemType}))),e.SET_USERINFO(Object(i["a"])({},t));var s=e.$router.options.routes.filter((function(e){return"/"===e.path}))[0].children;s=s.filter((function(e){return t.roles.filter((function(r){return e.meta.roles.includes(r)}))})),e.redirect=s[0].path})).finally((function(){setTimeout((function(){e.$router.push({path:e.redirect||"/"}),r.close()}),500)}))})).catch((function(){e.loading=!1}))}))},handleRegister:function(){var e=this;this.$refs.loginForm.validate((function(r){r&&e.registerUser(e.registerForm).then((function(r){200===r.code&&(e.$message.success("注册用户成功!"),e.tabValue="account",e.registerForm={username:"",nickname:"",email:"",password:""})}))}))}})},u=c,m=(t("daef"),t("2877")),d=Object(m["a"])(u,s,a,!1,null,"1047eb2a",null);r["default"]=d.exports},daef:function(e,r,t){"use strict";t("4ede")},fe63:function(e,r,t){e.exports=t.p+"static/img/bg.d718d295.png"}}]);