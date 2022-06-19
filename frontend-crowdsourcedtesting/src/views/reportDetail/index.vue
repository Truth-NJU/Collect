<template>
  <div class="app-container">
    <el-card class="box-card" shadow="always">
      <div slot="header" class="clearfix">
        <span><b>报告详情</b></span>
        <div style="float: right; padding: 3px 0">
          <el-rate
            v-model="form.star"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}"
          />
        </div>
        <el-button v-if="showSimialr" type="text" size="small" style="margin-left: 10px;" @click="findSimilarReports">查看推荐报告</el-button>
<!--        <el-button type="text" size="small" style="margin-left: 10px;" @click="findHistory()">查看历史报告</el-button>-->
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
            <el-button type="primary" @click="closeHistory">关 闭</el-button>
          </div>
        </el-dialog>
        <el-button type="text" size="small" style="margin-left: 10px;" @click="showcoworkers()">查看报告协作者</el-button>
        <div style="margin-top: 15px">
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
          <el-dialog title="报告协作" :visible="showRecommendReports" :show-close="false">
            <el-select v-if="showQuality" v-model="value" size="medium" style="margin-bottom: 10px" placeholder="请选择" @change="changeReport">
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
              <el-button type="primary" @click="closeDialog">关 闭</el-button>
            </div>
          </el-dialog>
          <el-form ref="form" :model="form" label-width="120px">
            <el-card class="box-card" shadow="hover" style="margin-bottom: 10px">
              <div slot="header" class="clearfix">
                <span><b>缺陷情况说明</b></span>
              </div>
              <div v-for="(item,index) in form.desc1" :key="item">
                {{ item }}
                <el-tooltip v-if="showButton" class="item" effect="dark" content="添加批注" placement="bottom">
                  <el-button v-if="showButton" icon="el-icon-edit" circle size="mini" @click="showInput(index,1)" />
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="查看所有批注" placement="bottom">
                  <el-button icon="el-icon-search" circle size="mini" @click="showAllAnnotation(index,1)" />
                </el-tooltip>
                <el-dialog title="添加批注" :visible="form.showText1[index]">
                  <el-input v-model="annotation" placeholder="请输入批注～" :autosize="{ minRows: 2, maxRows: 4}" maxlength="500" show-word-limit />
                  <div slot="footer" class="dialog-footer">
                    <el-button @click="cancel(index,1)">取 消</el-button>
                    <el-button type="primary" @click="addAnnotation(index,1)">确 定</el-button>
                  </div>
                </el-dialog>
                <el-dialog title="所有批注" :visible="form.showDrawer1[index]" :show-close="false">
                  <el-card v-for="item2 in form.annotation1[index]" :key="item2" class="box-card" style="margin-bottom: 10px">
                    <div class="text item">
                      {{ item2 }}
                    </div>
                  </el-card>
                  <div slot="footer" class="dialog-footer">
                    <el-button type="primary" @click="close(index,1)">关 闭</el-button>
                  </div>
                </el-dialog>
              </div>
            </el-card>
            <el-card class="box-card" shadow="hover" style="margin-bottom: 10px">
              <div slot="header" class="clearfix">
                <span><b>缺陷复现步骤</b></span>
              </div>
              <div v-for="(item,index) in form.desc2" :key="item" style="margin: 10px;">
                {{ item }}
                <el-tooltip v-if="showButton===true" class="item" effect="dark" content="添加批注" placement="bottom">
                  <el-button icon="el-icon-edit" circle size="mini" @click="showInput(index,2) " />
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="查看所有批注" placement="bottom">
                  <el-button icon="el-icon-search" circle size="mini" @click="showAllAnnotation(index,2)" />
                </el-tooltip>
                <el-dialog title="添加批注" :visible="form.showText2[index]">
                  <el-input v-model="annotation" placeholder="请输入批注～" :autosize="{ minRows: 2, maxRows: 4}" maxlength="500" show-word-limit />
                  <div slot="footer" class="dialog-footer">
                    <el-button @click="cancel(index,2)">取 消</el-button>
                    <el-button type="primary" @click="addAnnotation(index,2)">确 定</el-button>
                  </div>
                </el-dialog>
                <el-dialog title="所有批注" :visible="form.showDrawer2[index]" :show-close="false">
                  <el-card v-for="item2 in form.annotation2[index]" :key="item2" class="box-card" style="margin-bottom: 10px">
                    <div class="text item">
                      {{ item2 }}
                    </div>
                  </el-card>
                  <div slot="footer" class="dialog-footer">
                    <el-button type="primary" @click="close(index,2)">关 闭</el-button>
                  </div>
                </el-dialog>
              </div>
            </el-card>
            <el-card class="box-card" shadow="hover" style="margin-bottom: 10px">
              <div slot="header" class="clearfix">
                <span><b>测试设备信息</b></span>
              </div>
              <div>
                {{ form.desc3 }}
              </div>
            </el-card>
            <el-card class="box-card" shadow="hover" style="margin-bottom: 10px">
              <div slot="header" class="clearfix">
                <span><b>测试报告</b></span>
                <el-button v-if="form.paths.length!==0" style="float: right; padding: 3px 0" type="text" target="_blank" @click="getFullPath(form.paths)">点击下载</el-button>
                <!--            <p v-if="report.paths===null"  style="float: right; padding: 3px 0">该用户暂无报告文件</p>-->
                <!--            <el-button style="float: right; padding: 3px 0" type="text">下载报告</el-button>-->
              </div>
              <el-image
                v-for="item in form.fileList"
                :key="item.url"
                :src="item.url"
                style="width: 200px"
              />
            </el-card>
          </el-form>
        </div></div></el-card>
    <el-card class="box-card" shadow="always" style="margin-top: 30px">
      <div slot="header" class="clearfix">
        <span><b>参与评论</b></span>
      </div>
      <el-input
        v-model="comment"
        type="textarea"
        :autosize="{ minRows: 2, maxRows: 4}"
        placeholder="请输入你对这个报告的评论～"
        maxlength="500"
        show-word-limit
      />
      <div style="margin: 15px">
        <el-rate
          v-model="star"
          style="float: left"
          allow-half
          show-score
        />
        <el-button type="danger" round size="small" style="float: right;margin-bottom: 10px" @click="giveMarkAndComment()">评论</el-button>
      </div>
    </el-card>
    <el-card class="box-card" shadow="always" style="margin-top: 30px">
      <div slot="header" class="clearfix">
        <span><b>评论区</b></span>
      </div>
      <el-card v-for="item in commentList" :key="item" class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span><b>{{ item.name }}</b></span>
          <div style="float: right; padding: 3px 0">
            <el-rate
              v-model="item.mark"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
          </div>
        </div>
        <div>
          <span>{{ item.comment }}</span>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import { getIdentity, getUserId } from '../../utils/auth'
import { imageUrl } from '../../utils/request'
import {
  giveMarkAndComment,
  workerReport,
  getCommentsUnderReport,
  giveAnnotation,
  findSimilarReportsFromSameTask, findLowQualityReport, findCoworkers
} from '../../api/report'
import { parseToObject, parseToText } from '../../utils/parseAnnotation'
import JSZip from 'jszip'
import FileSaver from 'file-saver'

export default {
  inject: ['reload'],
  data() {
    return {
      userId: null,
      taskId: null,
      userIdentity: null,
      isRouterAlive: true,
      annotation: '',
      showButton: false,
      showSimialr: true,
      form: {
        desc1: [],
        annotation1: [],
        showText1: [],
        showDrawer1: [],
        desc2: [],
        annotation2: [],
        showText2: [],
        showDrawer2: [],
        desc3: '',
        fileList: [],
        star: 0,
        starNum: 0,
        paths: []
      },
      images: [],
      flag: true,
      loading: false,
      comment: '',
      star: 0,
      reportId: 0,
      reportUserId: 0, // 当前报告发布者的id
      commentList: [],
      name: '测试报告',
      similarReports: [],
      showRecommendReports: false,
      showQuality: true,
      options: [{
        value: '选项1',
        label: '相似报告'
      }, {
        value: '选项2',
        label: '低质量报告'
      }],
      value: '选项1',
      coworkerList: [],
      showHistoryReports: false,
      historyReports: [],
      showCoworkers: false
    }
  },
  created() {
    this.taskId = this.$route.query.taskId
    this.userId = this.$route.query.userId
    workerReport(this.taskId, this.userId).then(res => {
      console.log(res)
      if (res.data !== null) {
        this.flag = false
        this.form.desc1 = parseToObject(res.data[0].note).passage
        this.form.annotation1 = parseToObject(res.data[0].note).annotation
        console.log(this.form.annotation1)
        for (let i = 0; i < this.form.desc1.length; i++) {
          this.form.showText1.push(false)
          this.form.showDrawer1.push(false)
        }
        this.form.desc2 = parseToObject(res.data[0].steps).passage
        this.form.annotation2 = parseToObject(res.data[0].steps).annotation
        for (let i = 0; i < this.form.desc2.length; i++) {
          this.form.showText2.push(false)
          this.form.showDrawer2.push(false)
        }
        this.form.desc3 = res.data[0].device
        this.form.paths = res.data[0].paths
        this.form.star = res.data[0].star
        this.form.starNum = res.data[0].starNum
        this.reportId = res.data[0].id
        this.reportUserId = res.data[0].userId
        const paths = res.data[0].paths
        // console.log(this.form.paths)
        if (paths !== undefined) {
          // 为upload传入初始数据，即之前上传的图片
          paths.forEach(p => {
            const url = imageUrl + p
            this.images.push({
              url: url
            })
          })
        }
        this.form.fileList = this.images
        getCommentsUnderReport(this.reportId).then(res => {
          this.commentList = res.data
        })
        // console.log(parseInt(getUserId()) + typeof parseInt(getUserId()))
        if (parseInt(getUserId()) !== this.reportUserId && getIdentity() !== '发包方') {
          this.showButton = true
          this.$forceUpdate()
        }
        if (parseInt(getUserId()) !== this.reportUserId && getIdentity() !== '发包方') {
          this.showSimialr = false
          this.$forceUpdate()
        } else if (getIdentity() === '发包方') {
          this.showSimialr = true
          this.showQuality = false
          this.$forceUpdate()
        }
      }
    })
  },
  methods: {
    fuck(file, fileList) {
      this.form.fileList = fileList
    },
    showInput(index, type) {
      if (type === 1) {
        this.form.showText1[index] = true
      } else {
        this.form.showText2[index] = true
      }
      this.$forceUpdate()
    },
    showAllAnnotation(index, type) {
      if (type === 1) this.form.showDrawer1[index] = true
      else this.form.showDrawer2[index] = true
      this.$forceUpdate()
    },
    close(index, type) {
      if (type === 1) this.form.showDrawer1[index] = false
      else this.form.showDrawer2[index] = false
      this.$forceUpdate()
    },
    cancel(index, type) {
      if (type === 1) {
        this.form.showText1[index] = false
      } else {
        this.form.showText2[index] = false
      }
      this.annotation = ''
      this.$forceUpdate()
    },
    addAnnotation(index, type) {
      if (type === 1) {
        this.form.showText1[index] = false
        if (this.annotation !== '') {
          this.form.annotation1[index].push(this.annotation)
        }
      } else {
        this.form.showText2[index] = false
        if (this.annotation !== '') {
          this.form.annotation2[index].push(this.annotation)
        }
      }
      this.annotation = ''
      this.$forceUpdate()
      giveAnnotation(this.reportId, this.userId, this.taskId,
        parseToText({ passage: this.form.desc1, annotation: this.form.annotation1 }),
        parseToText({ passage: this.form.desc2, annotation: this.form.annotation2 }),
        this.form.desc3, this.form.star, this.form.starNum, parseInt(getUserId())).then(() => {
      })
    },
    giveMarkAndComment() {
      giveMarkAndComment(this.reportId, getUserId(), this.star, this.comment).then(res => {
        // console.log(res)
        getCommentsUnderReport(this.reportId).then(res => {
          this.commentList = res.data
          // console.log(res)
        })
        this.$notify({
          title: '评论发布成功',
          type: 'success'
        })
        this.comment = ''
        this.form.star = res.data[0].star
        this.star = 0
        this.$forceUpdate()
      })
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
    findSimilarReports() {
      findSimilarReportsFromSameTask(this.reportId).then(res => {
        if (res.data.length !== 0) {
          this.similarReports = res.data
          for (let i = 0; i < this.similarReports.length; i++) {
            this.similarReports[i].similarity = this.similarReports[i].similarity.toFixed(2) * 100 + '%'
          }
          this.showRecommendReports = true
          this.$forceUpdate()
        } else {
          this.$message({
            message: '没有相似的报告呢'
          })
        }
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
      location.reload()
      this.$forceUpdate()
    },
    closeDialog() {
      this.showRecommendReports = false
      this.value = '选项1'
      this.$forceUpdate()
    },
    showcoworkers() {
      findCoworkers(this.taskId, this.userId).then(res => {
        var list = []
        for (let j = 0; j < res.data[0].coworkers.length; j++) {
          list.push(res.data[0].coworkers[j])
        }
        this.coworkerList = list
      })
      this.showCoworkers = true
    },
    closeHistory() {
      this.showRecommendReports = false
      this.showHistoryReports = false
    },
    closeCoworkers() {
      this.showCoworkers = false
    },
    findHistory() {
      workerReport(this.taskId, this.userId).then(res => {
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
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }
  .box-card {
    width: 100%;
  }
</style>

