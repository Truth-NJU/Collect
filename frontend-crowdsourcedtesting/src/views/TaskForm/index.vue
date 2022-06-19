<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="120px">
      <el-form-item label="任务名称">
        <el-input v-model="form.name" style="width: 20%;min-width:200px;" maxlength="20" show-word-limit type="text" />
      </el-form-item>
      <el-form-item label="任务简介">
        <el-input
          v-model="form.introduction"
          style="width: 80%;min-width:250px;"
          :rows="3"
          maxlength="999"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="需求人数">
        <el-input-number v-model="form.number" :min="1" :max="200" label="描述文字" />
      </el-form-item>
      <el-form-item label="任务截止时间">
        <el-date-picker
          v-model="form.date"
          type="date"
          placeholder="选择日期"
        />
      </el-form-item>
      <el-form-item label="任务类型">
        <el-select v-model="form.type" placeholder="请选择">
          <el-option
            v-for="item in taskType"
            :key="item.value"
            :label="item.value"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="测试设备类型">
        <el-select v-model="form.device" placeholder="请选择" @change="changeDevice(form.device)">
          <el-option
            v-for="item in deviceType"
            :key="item.value"
            :label="item.value"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="任务描述文件">
        <el-upload
          ref="upload"
          action="https://jsonplaceholder.typicode.com/posts/"
          :file-list="descList"
          :auto-upload="false"
          :on-change="(file, fileList) => handleFileType(file, fileList, descAllow, descList)"
          :on-remove="handleDescRemove"
          :limit="1"
        >
          <el-button
            slot="trigger"
            size="small"
            type="primary"
            :disabled="descList.length===1"
          >上传文件
          </el-button>
          <div slot="tip" class="el-upload__tip">支持"pdf", "doc/docx", "md", "txt", "png", "jpg/jpeg"</div>
        </el-upload>
      </el-form-item>
      <el-form-item label="测试目标">
        <el-upload
          ref="upload"
          action="https://jsonplaceholder.typicode.com/posts/"
          :file-list="testList"
          :auto-upload="false"
          :on-change="(file, fileList) => handleFileType(file, fileList, testAllow, testList)"
          :on-remove="handleApkRemove"
          :limit="1"
        >
          <el-button
            slot="trigger"
            size="small"
            type="primary"
            :disabled="testList.length===1"
          >上传文件
          </el-button>
          <div slot="tip" class="el-upload__tip">支持{{ deviceSupport }}</div>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button v-if="createOrupdate===false" v-loading="loading" :disabled="loading" type="primary" style="margin-right: 50px" @click="onSubmit">发布</el-button>
        <el-button v-if="createOrupdate===true" v-loading="loading" :disabled="loading" type="primary" style="margin-right: 50px" @click="update">更新</el-button>
        <el-button @click="onClear">清空</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

import { issue } from '@/api/task'
import { update } from '../../api/task'
import { getUserId } from '../../utils/auth'
import { maxFileSize } from '../../utils/validate'

export default {
  data() {
    return {
      // true 代表更新，false代表创建
      createOrupdate: true,
      taskType: [{
        value: '功能测试'
      }, {
        value: '性能测试'
      }, {
        value: '稳定性测试'
      }],
      deviceType: [{
        value: 'Windows'
      }, {
        value: 'Linux'
      }, {
        value: 'MacOS'
      }, {
        value: 'Android'
      }, {
        value: 'IOS'
      }, {
        value: 'HarmonyOS'
      }],
      form: {
        name: '',
        introduction: '',
        number: 1,
        date: '',
        type: '功能测试',
        device: 'Windows'
      },
      testList: [],
      descList: [],
      testAllow: ['exe', 'zip'],
      descAllow: ['pdf', 'doc', 'docx', 'md', 'txt', 'png', 'jpg', 'jpeg'],
      dictionary: { Windows: ['exe', 'zip'], Linux: ['zip'], MacOS: ['dmg', 'zip'], Android: ['apk', 'zip'], ios: ['ipa', 'zip'], HarmonyOS: ['hap', 'zip'] },
      deviceSupport: "'exe', 'zip'",
      testDelete: false,
      descDelete: false,
      loading: false
    }
  },
  mounted() {
    window.addEventListener('beforeunload', e => this.beforeunloadFn(e))
  },
  destroyed() {
    window.removeEventListener('beforeunload', e => this.beforeunloadFn(e))
    this.$store.commit('task/SET_UPDATE', false)
  },
  created() {
    // console.log(this.$store.getters.task.update)
    this.createOrupdate = this.$store.getters.task.update
    // console.log(this.$store.getters.task.taskForm)
    if (this.createOrupdate) {
      // console.log(this.$store.getters.task.taskForm)
      this.form = this.$store.getters.task.taskForm
      if (this.form.purl !== null) {
        this.descList.push({
          name: this.form.purl.substr(this.form.purl.lastIndexOf('/') + 1),
          url: this.form.purl
        })
      }
      if (this.form.aurl !== null) {
        this.testList.push({
          name: this.form.aurl.substr(this.form.aurl.lastIndexOf('/') + 1),
          url: this.form.aurl
        })
      }
    }
  },
  methods: {
    changeDevice(device) {
      this.testAllow = this.dictionary[device]
      this.deviceSupport = "'".concat(this.dictionary[device].join("','")).concat("'")
    },
    update() {
      if (this.checkForm()) {
        const data = new FormData()
        data.append('userId', getUserId())
        data.append('id', this.$store.getters.task.taskId)
        data.append('name', this.form.name)
        data.append('introduction', this.form.introduction)
        data.append('number', this.form.number)
        data.append('date', new Date(this.form.date).getTime().toString())
        if (this.form.type === '功能测试') {
          data.append('tag', (0).toString())
        } else if (this.form.type === '性能测试') {
          data.append('tag', (1).toString())
        } else if (this.form.type === '稳定性测试') {
          data.append('tag', (2).toString())
        }
        data.append('device', (this.form.device).toString())
        if (this.testList.length === 1 && this.testDelete) { data.append('apk', this.testList[0].raw) }
        if (this.descList.length === 1 && this.descDelete) { data.append('pdf', this.descList[0].raw) }
        data.append('deleteApk', this.testDelete)
        data.append('deletePdf', this.descDelete)
        this.loading = true
        update(data).then(() => {
          this.$notify({
            title: '提示',
            message: '更新成功',
            type: 'success'
          })
          this.$router.push({ path: '/myTask/index' })
        }).finally(() => { this.loading = false })
      } else {
        this.$message({ message: '任务输入格式错误', type: 'error' })
      }
    },
    onSubmit() {
      if (this.checkForm()) {
        const data = new FormData()
        data.append('name', this.form.name)
        data.append('introduction', this.form.introduction)
        data.append('number', this.form.number)
        data.append('date', new Date(this.form.date).getTime().toString())
        if (this.form.type === '功能测试') {
          data.append('tag', (0).toString())
        } else if (this.form.type === '性能测试') {
          data.append('tag', (1).toString())
        } else if (this.form.type === '稳定性测试') {
          data.append('tag', (2).toString())
        }
        data.append('device', (this.form.device).toString())
        if (this.testList.length === 1) { data.append('apk', this.testList[0].raw) }
        if (this.descList.length === 1) { data.append('pdf', this.descList[0].raw) }
        this.loading = true
        issue(data).then(() => {
          this.$notify({
            title: '提示',
            message: '发布成功',
            type: 'success'
          })
          this.$router.push({ path: '/' })
        }).finally(() => { this.loading = false })
      } else {
        this.$message({ message: '任务输入格式错误', type: 'error' })
      }
    },
    onClear() {
      this.form = this.$options.data().form
    },
    checkForm() {
      return this.form.name.length > 0 && this.form.introduction.length > 0 && new Date(this.form.date).getTime() - new Date() > 0
    },
    handleFileType(file, fileList, type, save) {
      let fit = false
      if (file.size > maxFileSize) {
        this.$message({ message: '文件大小应低于2GB', type: 'error' })
        fileList.pop()
        return
      }
      if (file.name.lastIndexOf('.') !== -1) {
        const ext = file.name.substr(file.name.lastIndexOf('.') + 1)
        type.forEach(t => {
          fit |= t === ext
        })
      }
      if (!fit) {
        this.$message({ message: '仅支持\"' + type + '\"的文件类型', type: 'error' })
        fileList.pop()
      } else {
        save.push(file)
      }
    },
    handleDescRemove() {
      this.descList.pop()
      this.descDelete = true
    },
    handleApkRemove(save, deleteItem) {
      this.testList.pop()
      this.testDelete = true
    },
    beforeunloadFn(e) {
      // console.log('reload')
      sessionStorage.setItem('_taskform', JSON.stringify(this.$store.getters.task.taskForm))
      sessionStorage.setItem('_update', this.$store.getters.task.update)
    }
  }
}
</script>

<style scoped>
.el-upload__tip {
  min-width: 200px;
}
</style>
