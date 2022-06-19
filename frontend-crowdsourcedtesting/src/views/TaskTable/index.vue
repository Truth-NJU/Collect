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
        <el-form-item label="描述文件">
          <el-link :href="now_task.purl" target="_blank">点击查看</el-link>
        </el-form-item>
        <el-form-item label="可运行文件">
          <a :href="now_task.aurl" target="_blank">点击下载</a>
        </el-form-item>
      </el-form>
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
    </el-dialog>

    <el-dialog
      center
      title="选择喜好"
      :visible.sync="first"
      width="70%"
      :show-close="false"
      :close-on-press-escape="false"
      :close-on-click-modal="false"
    >
      <el-form ref="preferForm" :rules="preferRules" :model="preferForm">
        <el-form-item label="常用设备" prop="device">
          <el-select v-model="preferForm.device" multiple :multiple-limit="3" placeholder="选择常用测试设备">
            <el-option
              v-for="item in devices"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="测试方向" prop="prefer">
          <el-select v-model="preferForm.prefer" multiple placeholder="选择主要测试方向">
            <el-option
              v-for="item in prefers"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="test">确认</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-form inline>
      <el-form-item style="width: fit-content">
        <el-select v-model="state" placeholder="任务状态" style="width: 150px;" clearable @change="search_">
          <el-option
            v-for="item in states"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item style="width: fit-content">
        <el-select v-model="tag" placeholder="任务标签" clearable style="width: 150px;margin-left: 20px" @change="search_">
          <el-option
            v-for="item in tags"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item style="margin-left: 50px;width: 25%">
        <el-input v-model="name" placeholder="搜索任务名称" style="width:250px" @keyup.native="search" />
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

import { getAllTask, selectTaskByLabel } from '@/api/table'
import { checkWhetherPartTheTask, findTaskByTaskId, partTask } from '../../api/table'
import { getUserId, getIdentity, getFirst } from '../../utils/auth'
import { imageUrl } from '../../utils/request'
import request from '@/utils/request'

export default {
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
    const validateDevice = (rule, value, callback) => {
      if (value.length === 0) { callback(new Error('请选择一项')) } else { callback() }
    }
    const validatePrefer = (rule, value, callback) => {
      if (value.length === 0) { callback(new Error('请选择一项')) } else { callback() }
    }
    return {
      tag: '',
      tags: [{ value: 0, label: '功能测试' }, { value: 1, label: '性能测试' }],
      state: '',
      states: [{ value: 0, label: '未结束' }, { value: 1, label: '已结束' }],
      name: '',
      list: null,
      listLoading: true,
      now_task: {
        name: '',
        introduction: '',
        aurl: '',
        purl: '',
        number: 0,
        remain: 0
      },
      dialogVisible: false,

      first: getIdentity() === '众包工人' ? getFirst() === 'false' : false,
      devices: [{ value: 0, label: 'Windows' }, { value: 1, label: 'Linux' }, { value: 2, label: 'MacOS' },
        { value: 3, label: 'Android' }, { value: 4, label: 'IOS' }, { value: 5, label: 'HarmonyOS' }],

      prefers: [{ value: 0, label: '功能测试' }, { value: 1, label: '性能测试' }, { value: 2, label: '稳定性测试' }],

      preferForm: {
        device: [],
        prefer: []

      },
      preferRules: {
        device: [{ required: true, trigger: 'blur', validator: validateDevice }],
        prefer: [{ required: true, trigger: 'blur', validator: validatePrefer }]
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    test() {
      this.$refs.preferForm.validate(valid => {
        if (valid) {
          var userId = getUserId()
          request({
            method: 'post',
            url: '/collect/user/init',
            data: {
              userId: userId,
              preferTask: this.preferForm.prefer,
              device: this.preferForm.device
            }
          }).then(res => {
            this.first = false
            this.$store.commit('user/SET_FIRST', true)
          })
        }
      })
    },
    fetchData() {
      this.listLoading = true
      getAllTask().then(response => {
        // console.log(response)
        this.list = response.data
        this.listLoading = false
      })
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
      var userId = getUserId()
      var userIdentity = 0
      if (getIdentity() === '众包工人') {
        userIdentity = 1
      } else if (getIdentity() === '发包方') {
        userIdentity = 2
      }
      // console.log(userId)
      // console.log(userIdentity)
      // 是发包方
      if (userIdentity === 2) {
        findTaskByTaskId(userId, row.id).then(res => {
          // 是发包方，且该任务是他发的，可以看到url
          if (res.data[0].aurl !== null || res.data[0].purl !== null) {
            this.dialogVisible = true
            const task = res.data[0]
            this.now_task.name = task.name
            this.now_task.introduction = task.introduction
            this.now_task.aurl = imageUrl + task.aurl
            this.now_task.purl = imageUrl + task.purl
            this.now_task.number = task.number
            this.now_task.remain = task.remain
          } else if (userIdentity === 2 && res.data[0].aurl === null && res.data[0].purl === null) {
            // 是发包方，但这个任务不是他发的，看不到领取和url
            this.$alert(row.introduction, '任务详情', {
              confirmButtonText: '关闭',
              center: true
            })
          }
        }).then(err => {
          console.log(err)
        })
      } else if (userIdentity === 1) {
        // 是众包工人
        checkWhetherPartTheTask(userId, row.id).then(res => {
          if (res.data[0].workStatus !== null) {
            // 是众包工人，已经领取过任务，不显示领取，显示url
            // 重新请求获得任务详细内容
            this.dialogVisible = true
            this.now_task.name = res.data[0].name
            this.now_task.introduction = res.data[0].introduction
            this.now_task.aurl = imageUrl + res.data[0].aurl
            this.now_task.purl = imageUrl + res.data[0].purl
            this.now_task.number = res.data[0].number
            this.now_task.remain = res.data[0].remain
          } else if (res.data[0].workStatus === null) {
            // 是众包工人，没有领取过任务,可以领取
            this.$confirm(row.introduction, '任务详情', {
              confirmButtonText: '领取',
              cancelButtonText: '取消',
              type: 'info',
              center: true
            }).then(res => {
              if (row.remain >= 1) {
                partTask(userId, row.id).then(res => {
                  row.remain--
                  this.$message({
                    type: 'success',
                    message: '领取成功!'
                  })
                })
              }
              else {
                this.$message({
                  type: 'error',
                  message: '人数已满'
                })
              }
            })
          }
        })
      } else {
        this.$alert(row.introduction, '任务详情', {
          confirmButtonText: '关闭',
          center: true
        })
      }
    },

    search() {
      this.starttime = event.timeStamp
      const _this = this
      const e = event
      setTimeout(function() {
        if (_this.starttime - e.timeStamp === 0) {
          // 你的操作....
          selectTaskByLabel(_this.tag, _this.state, _this.name)
            .then(res => {
              _this.list = res.data
              _this.listLoading = false
            })
        }
      }, 800)
    },
    search_() {
      selectTaskByLabel(this.tag, this.state, this.name)
        .then(res => {
          this.list = res.data
          this.listLoading = false
        })
    }
  }
}
</script>
