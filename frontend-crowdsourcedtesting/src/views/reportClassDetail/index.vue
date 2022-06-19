<template>
  <div class="app-container">
    <el-table
      :data="list"
      border
      element-loading-text="Loading"
      fit
      highlight-current-row
      style="width: 100%"
    >
      <el-table-column
        prop="id"
        label="报告id"
      />
      <el-table-column
        prop="name"
        label="报告发布者"
      />
      <el-table-column
        prop="email"
        label="邮箱"
      />
      <el-table-column
        prop="star"
        label="报告评分"
      />
      <el-table-column
        prop="starNum"
        label="报告评分人数"
      />
      <el-table-column
        fixed="right"
        label="查看详情"
      >
        <template slot-scope="scope">
          <el-button
            type="primary"
            size="small"
            @click="reportDetail(scope.row.userId)"
          >
            查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { lookReports } from '../../api/report'

export default {
  data() {
    return {
      reportList: [],
      taskId: 0,
      list: []
    }
  },
  mounted() {
    this.reportList = JSON.parse(sessionStorage.getItem('_data')).reportList
    this.taskId = JSON.parse(sessionStorage.getItem('_data')).taskId
    lookReports(this.taskId).then(res => {
      // console.log(res.data)
      for (let j = 0; j < this.reportList.length; j++) {
        for (let i = 0; i < res.data.length; i++) {
          if (res.data[i].id === this.reportList[j]) {
            this.list.push(res.data[i])
          }
        }
      }
      // console.log(this.list)
    })
  },
  methods: {
    reportDetail(userId) {
      this.$router.push({
        path: '/reportDetail/index',
        query: {
          taskId: this.taskId,
          userId: userId
        }
      })
    }
  }
}
</script>

<style>
  .face{
    width: 80%;
    align-self: center;
  }

  .list{
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    padding: 4% 2% 4% 2%;
  }
  .book{
    min-width: 20%;
    max-width: 40%;
    width: 20%;
    min-height: 40%;
    max-height: 40%;
    margin: 4% 2% 4% 2%;
    display: flex;
    flex-direction: column;
    align-content: center;
    align-items: center;
    font-size:13px;
  }
</style>

