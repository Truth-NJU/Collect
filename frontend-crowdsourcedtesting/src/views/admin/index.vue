<template>
  <div>
    <el-card class="box-card">
      <div slot="header">
        <span style="font-weight: bold">管理推荐算法</span>
      </div>
      <div v-for="func in functions" :key="func">
        <h5 v-if="func.valid">当前的推荐算法是：{{ func.name }}</h5>
      </div>
      <el-collapse>
        <el-collapse-item v-for="item in functions" :key="item.id" :title="item.name">
          <div>
            {{ item.hint }}
          </div>
          <div style="margin-top: 10px">
            <el-checkbox-group v-if="item.id===1" v-model="attribute">
              <el-checkbox-button label="448">任务类型</el-checkbox-button>
              <el-checkbox-button label="63">任务设备</el-checkbox-button>
              <el-checkbox-button label="7680">用户能力</el-checkbox-button>
            </el-checkbox-group>
          </div>
          <el-button type="primary" style="margin-top: 10px" @click="change(item.id)">确认</el-button>
        </el-collapse-item>
      </el-collapse>

    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {

  name: "index.vue",
  created() {
    request({
      method: 'get',
      url: '/collect/user/getRules',
    }).then(res => {
      this.functions = res.data
    })
  },

  data() {
    return {
      flag: false,
      attribute: [],
      functions: [],
      ruleVOS: []
    }
  },
  created() {
    console.log('wndmd')
    request({
      method: 'get',
      url: '/collect/user/getRules'
    }).then(res => {
      this.functions = res.data
      console.log(res)
    })
  },
  methods: {
    change(id) {
      const selected = {
        id: id,
        extra: null
      }
      switch (id) {
        case 1: {
          if (this.attribute.length === 0) {
            this.$message.error({
              message: '请至少选择一个属性'
            })
            return
          }
          let num = 0
          for (let i = 0; i < this.attribute.length; i++) {
            num += parseInt(this.attribute[i])
          }
          selected.extra = { attributeMask: num }
          break
        }
      }
      this.ruleVOS.push(selected)
      request({
        method: 'post',
        url: '/collect/user/setRules',
        data: { ruleVOS: this.ruleVOS }
      })
      location.reload()
    }
  }
}
</script>

<style scoped>
.box-card {
  width: 80%;
  margin: 10px;
}
</style>
