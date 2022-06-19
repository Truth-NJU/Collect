<template>
  <div class="block">
    <el-timeline>
      <el-timeline-item
        v-for="(report, index) in reportList"
        :key="index"
      >
        <el-card style="font-size:13px">
          <div>
            <span style="font-size: 15px;font-weight: bold">上传报告用户信息:  </span>
            <span style="margin-left:15px">{{ report.name }}
              <el-tag type="warning" size="small">{{ report.workStatus }}</el-tag>
            </span>
            <el-divider direction="vertical" />
            <span>{{ report.email }}</span>
          </div>
          <div style="margin-top: 15px">
            <span style="font-size: 15px;font-weight: bold">测试设备信息:</span>
            <span style="margin:15px">{{ report.device }}</span>
          </div>
          <div style="margin-top: 15px">
            <span style="font-size: 15px;font-weight: bold">报告评分:</span>
            <span style="margin:15px">{{ report.star }}</span>
          </div>
          <div style="margin-top: 15px">
            <span style="font-size: 15px;font-weight: bold;margin-right: 20px">报告协作者:</span>
            <el-button type="primary" size="mini" @click="showcoworkers(report.userId)">查看</el-button>
            <el-dialog title="报告协作者" :visible="showCoworkers" :show-close="false">
              <el-table
                :data="coworkerList"
                stripe
                style="width: 100%"
              >
                <el-table-column
                  prop="name"
                  label="协作者"
                  width="180"
                />
                <el-table-column
                  prop="email"
                  label="协作者邮箱"
                  width="180"
                />
                <el-table-column
                  prop="userIdentity"
                  label="协作者身份"
                />
              </el-table>
              <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="closeCoworkers">关 闭</el-button>
              </div>
            </el-dialog>
          </div>
          <div style="margin-top: 15px">
            <span style="font-size: 15px;font-weight: bold;margin-right: 20px">查看历史报告:</span>
            <el-button type="primary" size="mini" @click="findHistory(report.userId)">查看</el-button>
          </div>
          <el-dialog title="历史报告" :visible="showHistoryReports" :show-close="false">
            <el-table
              :data="historyReports"
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
          <div style="margin-top: 15px">
            <span style="font-size: 15px;font-weight: bold;margin-right: 20px">查看相似报告:</span>
            <el-button type="primary" size="mini" @click="findSimilarity(report.id)">查看</el-button>
          </div>
          <el-dialog title="推荐报告" :visible="showRecommendReports" :show-close="false">
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
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script>
import { findCoworkers, findSimilarReportsFromSameTask, lookReports, workerReport } from '../../api/report'
import JSZip from 'jszip'
import FileSaver from 'file-saver'
import { imageUrl } from '../../utils/request'
import { parseNormal } from '../../utils/parseAnnotation'

export default {
  data() {
    return {
      taskId: null,
      reportList: null,
      name: '测试报告',
      coworkerList: [],
      showRecommendReports: false,
      similarReports: [],
      showHistoryReports: false,
      historyReports: [],
      showCoworkers: false
    }
  },
  created() {
    this.taskId = this.$store.getters.report.taskId
    lookReports(this.taskId).then(res => {
      // console.log(res.data)
      this.reportList = res.data
    })
  },
  methods: {
    showcoworkers(userId) {
      findCoworkers(this.taskId, userId).then(res => {
        var list = []
        for (let j = 0; j < res.data[0].coworkers.length; j++) {
          list.push(res.data[0].coworkers[j])
        }
        this.coworkerList = list
      })
      this.showCoworkers = true
    },
    getFullPath(path) {
      this.filesToRar(path, this.name)
    },
    /** 文件打包
             * arrImages:文件list:[Url:文件url...]
             * filename 压缩包名
             * */
    filesToRar(arrImages, filename) {
      const _this = this
      const zip = new JSZip()
      const cache = {}
      const promises = []
      _this.title = '正在加载压缩文件'
      let no = 1
      for (const url of arrImages) {
        const filename = no + url.substr(url.lastIndexOf('.'))
        no += 1
        const promise = _this.getImgArrayBuffer(imageUrl + url).then(data => {
          // 下载文件, 并存成ArrayBuffer对象(blob)
          zip.file(filename, data, { binary: true }) // 逐个添加文件
          cache[filename] = data
        })
        promises.push(promise)
      }

      Promise.all(promises).then(() => {
        zip.generateAsync({ type: 'blob' }).then(content => {
          _this.title = '正在压缩'
          // 生成二进制流
          FileSaver.saveAs(content, filename) // 利用file-saver保存文件  自定义文件名
          _this.title = '压缩完成'
        })
      }).catch(res => {
        _this.$message.error('文件压缩失败')
      })
    },
    // 获取文件blob
    getImgArrayBuffer(url) {
      return new Promise((resolve, reject) => {
        // 通过请求获取文件blob格式
        const xmlhttp = new XMLHttpRequest()
        xmlhttp.open('GET', url, true)
        xmlhttp.responseType = 'blob'
        xmlhttp.onload = function() {
          if (this.status === 200) {
            resolve(this.response)
          } else {
            reject(this.status)
          }
        }
        xmlhttp.send()
      })
    },
    deal(node) {
      return parseNormal(node)
    },
    findSimilarity(reportId) {
      findSimilarReportsFromSameTask(reportId).then(res => {
        // console.log(res.data)
        if (res.data.length === 0) {
          this.$router.push({
            path: '/reports/index'
          })
        }
        this.similarReports = res.data
        for (let i = 0; i < this.similarReports.length; i++) {
          this.similarReports[i].similarity = this.similarReports[i].similarity.toFixed(2) * 100 + '%'
        }
        this.showRecommendReports = true
        this.$forceUpdate()
      })
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
      this.showHistoryReports = false
      this.$router.push({
        path: '/reports/index'
      })
    },
    closeCoworkers() {
      this.showCoworkers = false
      this.$router.push({
        path: '/reports/index'
      })
    },
    findHistory(userId) {
      workerReport(this.taskId, userId).then(res => {
        if (res.data.length === 0) {
          this.$router.push({
            path: '/reports/index'
          })
        }
        this.historyReports = res.data
        this.showHistoryReports = true
        this.$forceUpdate()
      })
    }
  }
}
</script>
