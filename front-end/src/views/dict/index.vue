<template>
  <div>
    <div class="search-line">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="字典项标识符">
          <el-input v-model="searchForm.itemType" placeholder="请输入字典项标识符"></el-input>
        </el-form-item>
        <el-form-item label="字典项值">
          <el-input v-model="searchForm.itemValue" placeholder="请输入字典项值"></el-input>
        </el-form-item>
        <el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">查询</el-button>
          </el-form-item>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column label="标识符" prop="itemType"/>
      <el-table-column label="字典项值" prop="itemValue"/>
      <el-table-column label="支持流程" prop="supportFlow" :formatter="value => value && '支持'|| '不支持'"/>
      <el-table-column label="创建时间" prop="createTimeStr"/>
      <el-table-column label="操作" width="250px" align="center">
        <el-button @click="commit('reject')" type="danger">删除</el-button>
      </el-table-column>
    </el-table>

    <div style="margin-top: 5px;text-align: right;">
      <el-pagination
        small
        background
        :current-page.sync="pager.page"
        :page-size="pager.size"
        layout="prev, pager, next"
        :total="pager.total">
      </el-pagination>
    </div>
  </div>

</template>

<script>
import backendStyle from '../../utils/generic-backend-style-util'
import dict from "@/api/dict";

export default {
  name: "index",
  data() {
    return {
      searchForm: {
        itemType: '',
        itemValue: ''
      },
      tableData: [],
      ...backendStyle.data()
    }
  },

  created() {
    this.getDataFunc()
  },
  methods: {
    ...backendStyle.methods,
    ...dict.methods,
    getDataFunc() {
      this.getAllDictByPage(this.searchForm, this.pager).then(({result}) => {
        this.tableData = result.content;
      })
    },
    commit(val) {
      if (val === 'allow') {
        this.$message("调用接口")
      } else if (val === 'reject') {
        this.$message("驳回")
      }
    }
  }
}
</script>

<style scoped>

</style>
