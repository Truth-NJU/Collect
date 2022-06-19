<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="120px">
      <el-form-item label="缺陷情况说明">
        <el-input v-model="form.desc1" type="textarea" :autosize="{ minRows: 4, maxRows: 6}" />
      </el-form-item>
      <el-form-item label="缺陷复现步骤">
        <el-input v-model="form.desc2" type="textarea" :autosize="{ minRows: 4, maxRows: 6}" />
      </el-form-item>
      <el-form-item label="测试设备信息">
        <el-input v-model="form.desc3" type="textarea" :autosize="{ minRows: 4, maxRows: 6}" />
      </el-form-item>
      <el-form-item label="测试报告">
        <el-upload
          ref="upload"
          class="upload-demo"
          action="#"
          :on-change="fuck"
          :on-remove="fuck"
          :auto-upload="false"
          :file-list="images"
          list-type="picture"
        >
          <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
        </el-upload>
        <!--        <el-form-item-->
        <!--          v-for="(item,index) in images"-->
        <!--          :key="item"-->
        <!--        >-->
        <!--          <div class="images">-->
        <!--            <el-button class="button" icon="el-icon-delete" circle @click="deleteImage(index)" />-->
        <!--            <el-image-->
        <!--              style="width: 10%"-->
        <!--              :src="item"-->
        <!--            />-->
        <!--          </div>-->
        <!--        </el-form-item>-->
      </el-form-item>

      <br><br>
      <el-form-item>
        <el-button v-loading="loading" :disabled="loading" type="primary" @click="onSubmit">创建</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
    <el-dialog title="推荐报告" :visible="showRecommendReports" :show-close="false">
      <el-select v-model="value" size="medium" style="margin-bottom: 10px" placeholder="请选择" @change="changeReport">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-table
        :data="similarReports"
        stripe
        style="width: 100%"
      >
        <el-table-column
          prop="name"
          label="报告发布者"
          width="180"
        />
        <el-table-column
          prop="email"
          label="发布者邮箱"
          width="180"
        />
        <el-table-column
          prop="star"
          label="报告评分"
        />
        <el-table-column
          v-if="value==='选项1'"
          prop="similarity"
          label="报告相似度"
        />
        <el-table-column
          fixed="right"
          label="操作"
          width="100"
        >
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="reportDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="close">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'
import { getIdentity, getUserId } from '../../utils/auth'
import { baseUrl, imageUrl } from '../../utils/request'
import { maxImageSize } from '../../utils/validate'
import { findLowQualityReport, findSimilarReportsFromSameTask, workerReport } from '../../api/report'
import { parseNormal } from '../../utils/parseAnnotation'

export default {
  inject: ['reload'],
  data() {
    return {
      userId: null,
      taskId: null,
      userIdentity: null,
      isRouterAlive: true,
      showRecommendReports: false,
      form: {
        desc1: '',
        desc2: '',
        desc3: '',
        fileList: []
      },
      images: [],
      loading: false,
      similarReports: [],
      options: [{
        value: '选项1',
        label: '相似报告'
      }, {
        value: '选项2',
        label: '低质量报告'
      }],
      value: '选项1',
      reportId: 0
    }
  },
  created() {
    this.taskId = this.$route.query.taskId
    this.userId = getUserId()
    this.userIdentity = getIdentity()
    workerReport(this.taskId, this.userId).then(res => {
      // console.log(res)
      if (res.data !== null) {
        this.flag = false
        this.form.desc1 = parseNormal(res.data[0].note)
        this.form.desc2 = parseNormal(res.data[0].steps)
        this.form.desc3 = res.data[0].device
        this.form.toKeep = res.data[0].paths
        const paths = res.data[0].paths
        if (paths !== undefined) {
          // 为upload传入初始数据，即之前上传的图片
          paths.forEach(p => {
            const url = imageUrl + p
            this.images.push({
              url: url,
              name: url.substr(url.lastIndexOf('/') + 1),
              // 图片的原地址
              origin: p
            })
          })
        }
        this.form.fileList = this.images
      }
    })
  },
  methods: {
    // deleteImage(key) {
    //   console.log(key)
    //   this.images = this.images.slice(0, key).concat(this.images.slice(key + 1))
    //   this.form.toKeep = this.form.toKeep.slice(0, key).concat(this.form.toKeep.slice(key + 1))
    // },
    // upload的onchange方法
    fuck(file, fileList) {
      this.form.fileList = fileList
    },
    onSubmit() {
      // console.log(this.form.desc1 + this.form.desc2 + this.form.desc3 + this.form.fileList.length)
      // this.$message('submit!')
      const data = new FormData()
      data.set('userId', this.userId)
      data.append('taskId', this.taskId)
      data.append('note', this.form.desc1)
      data.append('steps', this.form.desc2)
      data.append('device', this.form.desc3)
      this.form.fileList.forEach(f => {
        if (f.size > maxImageSize) {
          this.$message({ message: '图片大小应低于24MB', type: 'error' })
          return
        }
        if (f.raw !== undefined) {
          data.append('images', f.raw)
        }
        // 如果是之前上传的图片，则在created中注入origin属性，此时仍保留的image's origin即为tokeep
        if (f.origin !== undefined) {
          data.append('toKeep', f.origin)
        }
      })
      this.loading = true
      axios.post(baseUrl + '/collect/report/commit', data, {
        headers: { 'Content-Type': 'multipart/reportForm-data' }
      }).then(res => {
        this.$notify({
          title: '报告发布成功',
          type: 'success'
        })
        const reportId = res.data.data[0].id
        this.reportId = reportId
        findSimilarReportsFromSameTask(reportId).then(res => {
          // console.log(res.data)
          if (res.data.length === 0) {
            this.$router.push({
              path: '/myTask/index'
            })
          }
          this.similarReports = res.data
          for (let i = 0; i < this.similarReports.length; i++) {
            this.similarReports[i].similarity = this.similarReports[i].similarity.toFixed(2) * 100 + '%'
          }
          this.showRecommendReports = true
          this.$forceUpdate()
        })
      }).finally(() => { this.loading = false })
      // this.reload()
    },
    changeReport() {
      // console.log(this.value)
      if (this.value === '选项1') {
        findSimilarReportsFromSameTask(this.reportId).then(res => {
          if (res.data.length === 0) {
            this.$notify.info({
              title: '提示',
              message: '暂无相似报告'
            })
          } else {
            this.similarReports = res.data
            for (let i = 0; i < this.similarReports.length; i++) {
              this.similarReports[i].similarity = this.similarReports[i].similarity.toFixed(2) * 100 + '%'
            }
            this.showRecommendReports = true
            this.$forceUpdate()
          }
        })
      } else {
        findLowQualityReport(this.reportId).then(res => {
          // console.log(res.data)
          if (res.data.length === 0) {
            this.$notify.info({
              title: '提示',
              message: '暂无低质量报告'
            })
          } else {
            this.similarReports = res.data
            this.showRecommendReports = true
            this.$forceUpdate()
          }
        })
      }
    },
    reportDetail(row) {
      this.$router.push({
        path: '/reportDetail/index',
        query: {
          taskId: row.taskId,
          userId: row.userId
        }
      })
    },
    close() {
      this.showRecommendReports = false
      this.$router.push({
        path: '/myTask/index'
      })
    },
    onCancel() {
      this.$message({
        message: 'cancel!',
        type: 'warning'
      })
      this.$router.push({
        path: '/myTask/index'
      })
    }
  }
}
</script>

<style scoped>
.images{
  width: 150px;
  display: inline;
}
.button{
  display: flex;
  align-content: center;
  position: absolute;
  left:0;
  width: 30px;
  height: 20px;
  z-index: 3;
  line-height: 5px;
  justify-content: center;
  align-items: center;
}
</style>

