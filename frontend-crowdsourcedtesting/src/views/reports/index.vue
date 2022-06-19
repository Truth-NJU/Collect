<template>
  <div v-if="isEnd">
    <div class="list">
      <el-card
        v-for="(report, index) in reportClassList"
        :key="index"
        class="book"
        shadow="hover"
        :body-style="{ padding: '0px' }"
        @click.native="jumpToClassDetail(report)"
      >
        <div
          class="book-content"
        >
          <el-image
            :src="faceList[index]"
            :fit="fit"
            class="face"
          />
          <el-divider />
          <i class="name el-icon-folder-opened">  分类 {{ index+1 }}</i>
        </div>
      </el-card>
    </div>
    <!--    <el-pagination-->
    <!--      layout="prev, pager, next"-->
    <!--      page-size="8"-->
    <!--      :total= reportClassList.length-->
    <!--      class="pagination"-->
    <!--      background-->
    <!--      @current-change="currentChange"-->
    <!--    />-->
  </div>
  <div v-else class="app-container">
    <el-table
      :data="reportList"
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
import { getReportClassUnderTask, lookReports } from '../../api/report'
import { imageUrl } from '../../utils/request'

export default {
  data() {
    return {
      reportClassList: null,
      faceList: null,
      url: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',
      taskId: 0,
      isEnd: true,
      reportList: null
    }
  },
  created() {
    this.taskId = JSON.parse(sessionStorage.getItem('_data')).taskId
    getReportClassUnderTask(this.taskId).then(res => {
      // console.log(res.data[0].path)
      // console.log(res.data[0].result)
      this.reportClassList = res.data[0].result
      this.faceList = res.data[0].path
      for (let i = 0; i < this.faceList.length; i++) {
        this.faceList[i] = imageUrl + this.faceList[i]
      }
      // console.log(this.faceList)
      this.isEnd = true
    }).catch(() => {
      this.isEnd = false
      lookReports(this.taskId).then(res => {
        // console.log(res.data)
        this.reportList = res.data
      })
    })
  },
  methods: {
    jumpToClassDetail(reportList) {
      const data = { reportList: reportList, taskId: this.taskId } // 需要传递的参数
      // sessionStorage存值
      sessionStorage.setItem('_data', JSON.stringify(data))
      // console.log(reportList)
      this.$router.push({
        path: '/reportClassDetail/index'
      })
    },
    reportDetail(userId) {
      this.$router.push({
        path: '/reportDetail/index',
        query: {
          taskId: this.taskId,
          userId: userId
        }
      })
    }
    // currentChange(val) {
    //   for (let i = 1; i <= 8; i++) {
    //     this.reportClassList[i - 1] = (val - 1) * 8 + i
    //   }
    //   this.$forceUpdate()
    // }
  }
}
</script>

<style>
  .face {
    width: 85%;
    align-self: center;
    margin-bottom: 4px;
  }

  .list {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    padding: 1% 2% 4% 2% !important;
  }

  .book {
    min-width: 20%;
    max-width: 40%;
    width: 20%;
    min-height: 40%;
    max-height: 40%;
    height: 40%;
    margin: 3% 2% 1% 2%;
    font-size: 14px;
  }

  .book-content {
    display: flex;
    flex-direction: column;
    align-content: center;
    align-items: center;
    padding: 0;
  }

  .pagination {
    position: fixed;
    bottom: 0;
    height: 10%;
    width: 100%;
    display: inline-flex;
    justify-content: center;
    align-items: center;
  }

  .name {
    font-size: 16px;
    line-height: 32px;
  }

  .el-divider--horizontal{
    margin-top: 8px!important;
    margin-bottom: 0px!important;
  }
</style>
