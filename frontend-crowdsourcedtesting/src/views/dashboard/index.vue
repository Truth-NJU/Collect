<template>
  <div class="app-container">

    <el-dialog
      center
      title="任务详情"
      :visible.sync="dialogVisible"
      width="70%"
    >

      <el-form inline>
        <el-form-item label="任务名称">
          {{ now_task.name }}
        </el-form-item>
        <el-form-item label="总人数/剩余人数" style="margin-left: 50px">
          {{ now_task.number }}/{{ now_task.remain }}
        </el-form-item>
      </el-form>

      <el-form>
        <el-form-item label="任务详情">
          {{ now_task.introduction }}
        </el-form-item>
        <el-form-item v-if="pNotNull===true" label="描述文件">
          <el-link :href="now_task.purl" target="_blank">点击查看</el-link>
        </el-form-item>
        <el-form-item v-if="pNotNull===false" label="描述文件">
          没有描述文件
        </el-form-item>
        <el-form-item v-if="aNotNull===true" label="可运行文件">
          <a :href="now_task.aurl" target="_blank">点击下载</a>
        </el-form-item>
        <el-form-item v-if="aNotNull===false" label="描述文件">
          没有可执行文件
        </el-form-item>
      </el-form>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button v-if="checkTime===true" type="primary" @click="editTask(now_task.id)">{{ diff_button }}</el-button>
      <el-button v-if="userIdentity===1" @click="dialogVisible = checkReportsOfOwn(now_task.id)">查看报告</el-button>
      <el-button v-if="userIdentity===2" @click="dialogVisible = checkReports(now_task.id)">查看报告</el-button>
    </el-dialog>

    <el-form inline>
      <el-form-item style="width: fit-content">
        <el-select v-model="state" placeholder="任务状态" style="width: 150px;" @change="changeList()">
          <el-option
            v-for="item in states"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column label="任务名称" width="110">
        <template slot-scope="scope">
          {{ scope.row.name }}
        </template>
      </el-table-column>
      <el-table-column label="任务简介">
        <template slot-scope="scope">
          {{ scope.row.introduction | ellipsis }}
        </template>
      </el-table-column>
      <el-table-column label="总共所需人数" width="110" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.number }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余所需人数" width="110" align="center">
        <template slot-scope="scope">
          {{ scope.row.remain }}
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="任务标签" width="110" align="center">
        <template slot-scope="scope">
          {{ parse(scope.row.tag) }}
        </template>
      </el-table-column>
      <el-table-column label="任务设备" width="110" align="center">
        <template slot-scope="scope">
          {{ scope.row.device }}
        </template>
      </el-table-column>
      <el-table-column align="center" prop="created_at" label="任务截止日期" width="200">
        <template slot-scope="scope">
          <i class="el-icon-time" />
          <span>{{ format(scope.row.date) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="95">
        <template slot-scope="scope">
          <el-button @click="getDetails(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

  </div>
</template>

<script>
import { getMyTask } from '@/api/table'
import { checkWhetherPartTheTask, findTaskByTaskId, searchPublishedTask } from '../../api/table'
import { getIdentity, getUserId } from '../../utils/auth'
import { imageUrl } from '../../utils/request'

export default {
  name: 'Login',
  filters: {
    ellipsis(value) {
      if (!value) return ''
      if (value.length > 45) {
        return value.slice(0, 45) + '...'
      }
      return value
    }
  },
  data() {
    return {
      state: null,
      states: null,
      aNotNull: null, // 判断aurl是否为空
      pNotNull: null, // 判断purl是否为空
      list: null,
      checkTime: true,
      allTaskList: null, // 备份列表
      diff_button: null,
      listLoading: true,
      userId: getUserId(),
      userIdentity: 0,
      now_task: {
        name: '',
        introduction: '',
        aurl: '',
        purl: '',
        number: 0,
        remain: 0,
        id: 0,
        date: '',
        type: '',
        device: ''
      },
      dialogVisible: false
    }
  },
  created() {
    // 判断身份
    this.checkStatus()
    this.fetchData()
  },
  methods: {
    checkStatus() {
      if (getIdentity() === '众包工人') {
        this.userIdentity = 1
        this.diff_button = '编辑报告'
      } else {
        this.userIdentity = 2
        this.diff_button = '编辑任务'
      }
      if (this.userIdentity === 2) {
        this.state = 'All'
        this.states = [{ value: 'All', label: '所有任务' }, { value: 'UnderWay', label: '进行中' }, { value: 'FINISHED', label: '已结束' }]
      } else if (this.userIdentity === 1) {
        this.state = 'TO_FINISH'
        this.states = [{ value: 'TO_FINISH', label: '待完成' }, { value: 'FINISH', label: '已完成' }, { value: 'FAIL', label: '失败' }]
      }
    },
    fetchData() {
      this.listLoading = true
      if (getIdentity() === '发包方') {
        searchPublishedTask(getUserId()).then(res => {
          // console.log(res)
          if (res.data === null) {
            this.$notify({
              title: '提示',
              message: '没有发布任务',
              type: 'success'
            })
            this.listLoading = false
          }
          this.list = res.data
          this.allTaskList = res.data
          this.listLoading = false
        })
      } else if (getIdentity() === '众包工人') {
        getMyTask(getUserId(), this.state).then(response => {
          // console.log(response)
          if (response === null) this.listLoading = false
          this.list = response.data
          this.allTaskList = response.data
          this.listLoading = false
        })
      }
    },
    add(m) {
      return m < 10 ? '0' + m : m
    },
    format(shijianchuo) {
      var time = new Date(shijianchuo)
      var y = time.getFullYear()
      var m = time.getMonth() + 1
      var d = time.getDate()
      var h = time.getHours()
      var mm = time.getMinutes()
      var s = time.getSeconds()
      return y + '-' + this.add(m) + '-' + this.add(d) + ' ' + this.add(h) + ':' + this.add(mm) + ':' + this.add(s)
    },
    parse(tag) {
      if (tag === 0) return '功能测试'
      if (tag === 1) return '性能测试'
      if (tag === 2) return '稳定性测试'
    },

    getDetails(row) {
      // console.log(getUserId())
      // 是发包方
      if (this.userIdentity === 2) {
        findTaskByTaskId(this.userId, row.id).then(res => {
          // console.log(res.data[0])
          // 是发包方，且该任务是他发的，可以看到url
          if (res.data[0].date < new Date().getTime()) {
            this.checkTime = false
          } else {
            this.checkTime = true
          }
          this.dialogVisible = true
          const task = res.data[0]
          // console.log(task)
          this.now_task.id = res.data[0].id
          this.now_task.name = task.name
          this.now_task.introduction = task.introduction
          this.now_task.device = task.device
          if (task.aurl === null) {
            this.aNotNull = false
            this.now_task.aurl = null
          } else {
            this.aNotNull = true
            this.now_task.aurl = imageUrl + task.aurl
          }
          if (task.purl === null) {
            this.pNotNull = false
            this.now_task.purl = null
          } else {
            this.pNotNull = true
            this.now_task.purl = imageUrl + task.purl
          }
          this.now_task.number = task.number
          this.now_task.remain = task.remain
          this.now_task.date = task.date
          this.now_task.type = task.tag
        }).then(err => {
          console.log(err)
        })
      } else if (this.userIdentity === 1) {
        // 是众包工人
        checkWhetherPartTheTask(this.userId, row.id).then(res => {
          // 是众包工人，已经领取过任务，不显示领取，显示url
          // 重新请求获得任务详细内容
          this.dialogVisible = true
          const task = res.data[0]
          this.now_task.id = res.data[0].id
          this.now_task.name = res.data[0].name
          this.now_task.introduction = res.data[0].introduction
          this.now_task.device = res.data[0].device
          if (task.aurl === null) {
            this.aNotNull = false
            this.now_task.aurl = null
          } else {
            this.aNotNull = true
            this.now_task.aurl = imageUrl + task.aurl
          }
          if (task.purl === null) {
            this.pNotNull = false
            this.now_task.purl = null
          } else {
            this.pNotNull = true
            this.now_task.purl = imageUrl + task.purl
          }
          this.now_task.number = res.data[0].number
          this.now_task.remain = res.data[0].remain
          this.now_task.date = res.data[0].date
          this.now_task.type = res.data[0].tag
        })
      }
    },
    changeList() {
      var curTime = new Date().getTime()
      if (this.userIdentity === 2) {
        if (this.state === 'UnderWay') {
          var tmpList = []
          // 获得进行中的任务
          for (var i = 0; i < this.allTaskList.length; i++) {
            if (this.allTaskList[i].date >= curTime) {
              tmpList.push(this.allTaskList[i])
            }
          }
          this.list = tmpList
        } else if (this.state === 'FINISHED') {
          // 获得已结束的任务
          var tmpList2 = []
          // 获得进行中的任务
          for (var j = 0; j < this.allTaskList.length; j++) {
            if (this.allTaskList[j].date < curTime) {
              tmpList2.push(this.allTaskList[j])
            }
          }
          this.list = tmpList2
        } else if (this.state === 'All') {
          this.list = this.allTaskList
        }
      } else if (this.userIdentity === 1) {
        // 任务2失败、任务4已完成、3、5、13 待完成
        getMyTask(getUserId(), this.state).then(response => {
          // console.log(response)
          this.list = response.data
        })
      }
    },
    editTask(taskId) {
      this.dialogVisible = false
      // console.log(taskId)
      if (this.userIdentity === 2) {
        // 跳转到更新任务页面
        this.$store.commit('task/SET_TASKID', taskId)
        this.$store.commit('task/SET_UPDATE', true)
        this.$store.commit('task/SET_FORM', this.now_task)
        this.$router.push({
          path: '/taskForm/index'
        })
      } else if (this.userIdentity === 1) {
        // 跳转到报告编辑页面
        // this.$store.commit('report/SET_TASKID', taskId)
        this.$router.push({
          path: '/reportForm/index',
          query: {
            taskId: taskId
          }
        })
      }
    },
    checkReports(taskId) {
      this.dialogVisible = false
      this.$store.commit('report/SET_TASKID', taskId)
      const data = { taskId: taskId } // 需要传递的参数
      // sessionStorage存值
      sessionStorage.setItem('_data', JSON.stringify(data))
      // 跳转到查看报告页面
      this.$router.push({
        path: '/reports/index'
      })
    },
    // 众包工人查看自己在当前任务下的最新的报告的内容
    checkReportsOfOwn(taskId) {
      this.dialogVisible = false
      this.$store.commit('report/SET_TASKID', taskId)
      this.$router.push({
        path: '/reportDetail/index',
        query: {
          taskId: taskId,
          userId: getUserId()
        }
      })
    }
  }
}
</script>

