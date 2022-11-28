<template>
  <el-dialog
    :title="!dataForm.userId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="dataForm.username" placeholder="用户名"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password">
      <el-input v-model="dataForm.password" placeholder="密码"></el-input>
    </el-form-item>
    <el-form-item label="真实姓名" prop="realName">
      <el-input v-model="dataForm.realName" placeholder="真实姓名"></el-input>
    </el-form-item>
    <el-form-item label="电话号码" prop="phone">
      <el-input v-model="dataForm.phone" placeholder="电话号码"></el-input>
    </el-form-item>
    <el-form-item label="权限等级（1为普通员工）" prop="permissionLevel">
      <el-input v-model="dataForm.permissionLevel" placeholder="权限等级（1为普通员工）"></el-input>
    </el-form-item>
    <el-form-item label="base64编码的图片" prop="faceInfo">
      <el-input v-model="dataForm.faceInfo" placeholder="base64编码的图片"></el-input>
    </el-form-item>
    <el-form-item label="启用状态（0为禁用，1为启用）" prop="status">
      <el-input v-model="dataForm.status" placeholder="启用状态（0为禁用，1为启用）"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          userId: 0,
          username: '',
          password: '',
          realName: '',
          phone: '',
          permissionLevel: '',
          faceInfo: '',
          status: ''
        },
        dataRule: {
          username: [
            { required: true, message: '用户名不能为空', trigger: 'blur' }
          ],
          password: [
            { required: true, message: '密码不能为空', trigger: 'blur' }
          ],
          realName: [
            { required: true, message: '真实姓名不能为空', trigger: 'blur' }
          ],
          phone: [
            { required: true, message: '电话号码不能为空', trigger: 'blur' }
          ],
          permissionLevel: [
            { required: true, message: '权限等级（1为普通员工）不能为空', trigger: 'blur' }
          ],
          faceInfo: [
            { required: true, message: 'base64编码的图片不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '启用状态（0为禁用，1为启用）不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.userId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.userId) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/user/info/${this.dataForm.userId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.username = data.user.username
                this.dataForm.password = data.user.password
                this.dataForm.realName = data.user.realName
                this.dataForm.phone = data.user.phone
                this.dataForm.permissionLevel = data.user.permissionLevel
                this.dataForm.faceInfo = data.user.faceInfo
                this.dataForm.status = data.user.status
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/user/${!this.dataForm.userId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'userId': this.dataForm.userId || undefined,
                'username': this.dataForm.username,
                'password': this.dataForm.password,
                'realName': this.dataForm.realName,
                'phone': this.dataForm.phone,
                'permissionLevel': this.dataForm.permissionLevel,
                'faceInfo': this.dataForm.faceInfo,
                'status': this.dataForm.status
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
