(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-4e279aaf"],{"0cca":function(e,t,a){"use strict";var r=a("5530"),l=a("53ca"),o=function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"searchForm",a=this[t];"object"===Object(l["a"])(t)&&(a=t),null!=t&&a.startTimeAt&&a.endTimeAt&&(a.startTimeAt<a.endTimeAt||(this.$message.warning("结束时间必须大于开始时间 !!!"),a[e]=""))};t["a"]={methods:{validTimeRange:o,getSearchform:function(){var e,t,a=arguments.length>0&&void 0!==arguments[0]?arguments[0]:this.searchForm;return Object(r["a"])(Object(r["a"])({},a),{},{startTimeAt:null===(e=this.searchForm.startTimeAt)||void 0===e?void 0:e.getTime(),endTimeAt:null===(t=this.searchForm.endTimeAt)||void 0===t?void 0:t.getTime()})},onSubmit:function(){var e=this,t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"searchForm",a=this[t];"object"===Object(l["a"])(t)&&(a=t),this.getDataFunc(this.getSearchform(a)).then((function(t){e.pager.page>1&&t&&t.totalPages&&t.totalPages<=1&&(e.pager.page=1,e.onSubmit()),e.pager.total=(null===t||void 0===t?void 0:t.totalElements)||0})).catch((function(t){e.$message.warning((null===t||void 0===t?void 0:t.message)||"系统错误 !!!`")}))},onReset:function(){this.$refs["searchForm"].resetFields(),console.log(this.$refs)},onCancel:function(e){var t;this[e].visible=!1,null===(t=this.$refs[e])||void 0===t||t.resetFields()},entryKeyDown:function(e){this.onSubmit()},datepickerInputFocus:function(e){console.log("失去焦点"),this.$refs[e].focus()}},data:function(){return{pager:{page:1,size:10,total:0}}}}},"0cf1":function(e,t,a){},"9be8":function(e,t,a){"use strict";a("0cf1")},e382:function(e,t,a){"use strict";a.r(t);var r=function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",[a("div",{staticClass:"search-line"},[a("el-form",{staticClass:"search-form",attrs:{inline:!0,model:e.searchForm}},[a("el-form-item",{attrs:{label:"用户名称"}},[a("el-input",{attrs:{placeholder:"请输入用户名称",clearable:""},model:{value:e.searchForm.username,callback:function(t){e.$set(e.searchForm,"username",t)},expression:"searchForm.username"}})],1),a("el-form-item",{attrs:{label:"注册时间"}},[a("el-date-picker",{attrs:{type:"date",placeholder:"选择日期"},on:{change:function(t){return e.validTimeRange("startTimeAt")}},model:{value:e.searchForm.startTimeAt,callback:function(t){e.$set(e.searchForm,"startTimeAt",t)},expression:"searchForm.startTimeAt"}})],1),a("el-form-item",{attrs:{label:"结束时间"}},[a("el-date-picker",{attrs:{type:"date",placeholder:"选择日期"},on:{change:function(t){return e.validTimeRange("endTimeAt")}},model:{value:e.searchForm.endTimeAt,callback:function(t){e.$set(e.searchForm,"endTimeAt",t)},expression:"searchForm.endTimeAt"}})],1),a("el-form-item",[a("el-form-item",[a("el-button",{attrs:{type:"primary"},on:{click:e.onSubmit}},[e._v("查询")])],1)],1)],1)],1),a("el-divider",{staticClass:"margin-top-bottom-10"}),[a("div",{staticClass:"margin-top-bottom-10 margin-left-25"},[a("el-button",{attrs:{type:"success"},on:{click:function(t){return e.openDrawer(!1)}}},[e._v("创建用户")])],1),a("el-table",{staticStyle:{width:"100%"},attrs:{data:e.tableData}},[a("el-table-column",{attrs:{label:"昵称",prop:"nickname"}}),a("el-table-column",{attrs:{label:"姓名",prop:"username"}}),a("el-table-column",{attrs:{label:"角色",prop:"roles"},scopedSlots:e._u([{key:"default",fn:function(t){var a=t.row.roles;return[e._v(" "+e._s(a.map((function(e){return e.itemValue})))+" ")]}}])}),a("el-table-column",{attrs:{label:"邮箱",prop:"email"}}),a("el-table-column",{attrs:{label:"电话",prop:"phone"}}),a("el-table-column",{attrs:{label:"个人简介",prop:"description"}}),a("el-table-column",{attrs:{label:"性别"},scopedSlots:e._u([{key:"default",fn:function(t){var a=t.row.gex;return[e._v(" "+e._s(e.mapDictItemValue("genderStatus",a))+" ")]}}])}),a("el-table-column",{attrs:{label:"创建时间",prop:"createTimeStr"}}),a("el-table-column",{attrs:{label:"操作",width:"250px",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{type:"primary"},on:{click:function(a){return e.assignRolesToUser(t)}}},[e._v("分配角色")]),a("el-button",{attrs:{type:"danger"},on:{click:function(a){return e.deleteDialogHandle(t)}}},[e._v("删除")])]}}])})],1),a("div",{staticStyle:{"margin-top":"5px","text-align":"right"}},[a("el-pagination",{attrs:{small:"",background:"","current-page":e.pager.page,"page-size":e.pager.size,layout:"prev, pager, next",total:e.pager.total},on:{"update:currentPage":function(t){return e.$set(e.pager,"page",t)},"update:current-page":function(t){return e.$set(e.pager,"page",t)}}})],1)],a("el-drawer",{attrs:{title:e.drawerAction?"新增用户":"用户更新",visible:e.drawerFlag,size:"400px",direction:"rtl"},on:{"update:visible":function(t){e.drawerFlag=t}}},[a("div",{staticClass:"scroll-view"},[a("el-form",{staticClass:"search-form",staticStyle:{padding:"5px"},attrs:{size:"small",model:e.drawerDialogData}},[a("el-form-item",{attrs:{label:"用户名"}},[a("el-input",{attrs:{placeholder:"请输入用户名"},model:{value:e.drawerDialogData.username,callback:function(t){e.$set(e.drawerDialogData,"username",t)},expression:"drawerDialogData.username"}})],1),a("el-form-item",{attrs:{label:"昵称"}},[a("el-input",{attrs:{placeholder:"请输入昵称"},model:{value:e.drawerDialogData.nickname,callback:function(t){e.$set(e.drawerDialogData,"nickname",t)},expression:"drawerDialogData.nickname"}})],1),a("el-form-item",{attrs:{label:"个人简介"}},[a("el-input",{attrs:{type:"textarea",size:"medium",autosize:{minRows:3,maxRows:6},maxlength:"300","show-word-limit":"",placeholder:"请输入个人简介"},model:{value:e.drawerDialogData.description,callback:function(t){e.$set(e.drawerDialogData,"description",t)},expression:"drawerDialogData.description"}})],1),a("el-form-item",{attrs:{label:"email"}},[a("el-input",{attrs:{placeholder:"请输入邮箱信息"},model:{value:e.drawerDialogData.email,callback:function(t){e.$set(e.drawerDialogData,"email",t)},expression:"drawerDialogData.email"}})],1),a("el-form-item",{attrs:{label:"密码"}},[a("el-input",{attrs:{placeholder:"请输入密码信息"},model:{value:e.drawerDialogData.password,callback:function(t){e.$set(e.drawerDialogData,"password",t)},expression:"drawerDialogData.password"}})],1),a("el-form-item",{attrs:{label:"phone"}},[a("el-input",{attrs:{placeholder:"请输入手机号码"},model:{value:e.drawerDialogData.phone,callback:function(t){e.$set(e.drawerDialogData,"phone",t)},expression:"drawerDialogData.phone"}})],1),a("el-form-item",{attrs:{label:"性别"}},[a("el-select",{attrs:{placeholder:"请选择性别"},model:{value:e.drawerDialogData.gex,callback:function(t){e.$set(e.drawerDialogData,"gex",t)},expression:"drawerDialogData.gex"}},e._l(e.genderStatus,(function(e){var t=e.itemType,r=e.itemValue,l=e.id;return a("el-option",{key:t,attrs:{label:r,value:l}})})),1)],1),a("el-form-item",{attrs:{label:"出生日期"}},[a("el-date-picker",{attrs:{type:"date",placeholder:"请选择出生日期"},model:{value:e.drawerDialogData.birthday,callback:function(t){e.$set(e.drawerDialogData,"birthday",t)},expression:"drawerDialogData.birthday"}})],1),a("el-form-item",[a("el-form-item",{staticStyle:{"margin-top":"20px"}},[a("el-button",{staticStyle:{width:"100%"},attrs:{type:"primary"},on:{click:e.saveUser}},[e._v("提交")])],1)],1)],1)],1)]),a("el-dialog",{key:"assign-role",attrs:{title:"分配角色",visible:e.roleForm.visible,width:"60%"},on:{"update:visible":function(t){return e.$set(e.roleForm,"visible",t)},close:function(t){e.roleForm.visible=!1,e.roleForm.data={},e.clearRoleStatus()}}},[a("el-button",{attrs:{type:"primary",size:"small",disabled:e.roleForm.disabled},on:{click:e.subUserWithRoles}},[e._v("分配")]),a("el-table",{key:"assignedRolesTable",ref:"assignedRoles",attrs:{data:e.roles,"row-key":"id"},on:{"select-all":e.configUserRoles,select:e.configUserRoles}},[a("el-table-column",{attrs:{type:"selection","reserve-selection":!0}}),a("el-table-column",{attrs:{label:"角色",prop:"itemValue",align:"center"}})],1)],1)],2)},l=[],o=a("2909"),s=a("5530"),i=(a("d81d"),a("d3b7"),a("159b"),a("caad"),a("2532"),a("99af"),a("0cca")),n=a("b705"),c=a("c24f"),u=a("180d"),d=a("b775"),m="/api/admin/role/v1",p={updateRoleForUserUrl:"".concat(m,"/rru/update")},g={methods:{updateRoleForUser:function(e){return d["a"].post(p.updateRoleForUserUrl,e)}}},f=a("2f62"),h=a("5f87"),b={name:"Index",data:function(){return Object(s["a"])(Object(s["a"])({searchForm:{username:"",startTimeAt:null,endTimeAt:null},tableData:[],drawerFlag:!1,drawerAction:!1,drawerDialogData:{username:"",description:"",nickname:"",birthday:"",email:"",phone:"",gex:"",password:""},roleForm:{visible:!1,disabled:!0,data:{userId:"",roles:[]}},currentToggleRoleIds:[]},i["a"].data()),n["a"].data())},created:function(){this.getGenderStatus(),this.onSubmit(),this.getRoles()},methods:Object(s["a"])(Object(s["a"])(Object(s["a"])(Object(s["a"])(Object(s["a"])(Object(s["a"])(Object(s["a"])({auto:u["auto"]},i["a"].methods),n["a"].methods),c["a"].methods),g.methods),Object(f["d"])("user",["userInfo"])),Object(f["c"])("user",["SET_USERINFO"])),{},{getDataFunc:function(){var e=this;return this.getAllUsersByPage(this.getSearchform(),this.pager).then((function(t){var a=t.result;return e.tableData=a.content,a}))},defaultAssignRoles:function(){var e=this;this.$nextTick((function(){var t=[];if(e.roleForm.data.roles){var a=e.roleForm.data.roles.map((function(e){return e.id}));e.roles.forEach((function(e){a.includes(e.id)&&t.push(e)}))}t.forEach((function(t){e.currentToggleRoleIds.includes(t.id)||(e.$refs.assignedRoles.toggleRowSelection(t),e.currentToggleRoleIds=[].concat(Object(o["a"])(e.currentToggleRoleIds),[t.id]))}))}))},configUserRoles:function(e){this.roleForm.data.roles=e,this.roleForm.disabled=!(e.length>0)},saveUser:function(){var e=this;this.drawerAction?this.updateUser(this.drawerDialogData).then((function(){e.$message.success("更新成功 !!!"),e.getDataFunc()})):this.registerUser(this.drawerDialogData).then((function(){e.$message.success("注册成功 !!!"),e.getDataFunc()}))},subUserWithRoles:function(){var e=this;this.updateRoleForUser(Object(s["a"])(Object(s["a"])({},this.roleForm.data),{},{roleIds:this.roleForm.data.roles.map((function(e){return e.id}))})).then((function(){e.$message.success("分配成功 !!!"),e.onSubmit(),e.roleForm.data.userId===e.userInfo().id&&e.getCurrentUserInfo().then((function(t){var a=t.result;console.log((a.roles||[]).map((function(e){return e.itemType}))),Object(h["h"])((a.roles||[]).map((function(e){return e.itemType}))),e.SET_USERINFO(a)}))})).catch((function(t){console.log(t),e.$message.error("分配失败 !!!")})).finally((function(){e.clearRoleStatus(),e.roleForm.disabled=!0,e.roleForm.data={},e.roleForm.visible=!1}))},clearRoleStatus:function(){},openDrawer:function(e){e&&(this.drawerAction=!0),this.drawerFlag=!0},deleteDialogHandle:function(e){var t=this,a=e.row,r=a.id,l=a.username;this.$confirm("确定删除用户".concat(l,"?"),"提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then((function(){t.deleteUserById(r).then((function(e){t.$message({type:"success",message:"删除成功!"}),t.getDataFunc()}))})).catch((function(){}))},assignRolesToUser:function(e){var t=e.row;this.roleForm.visible=!0,this.roleForm.data.userId=t.id,this.roleForm.data.roles=t.roles||[],this.defaultAssignRoles()}})},v=b,D=(a("9be8"),a("2877")),w=Object(D["a"])(v,r,l,!1,null,"c3a2fd76",null);t["default"]=w.exports}}]);