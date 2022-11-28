<template>
  <el-dialog
    :title="!dataForm.meetingId ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="会议标题" prop="title">
      <el-input v-model="dataForm.title" placeholder="会议标题"></el-input>
    </el-form-item>
    <el-form-item label="会议开始时间" prop="startTime">
      <el-input v-model="dataForm.startTime" placeholder="会议开始时间"></el-input>
    </el-form-item>
    <el-form-item label="预计会议结束时间" prop="endTime">
      <el-input v-model="dataForm.endTime" placeholder="预计会议结束时间"></el-input>
    </el-form-item>
    <el-form-item label="最晚会议结束时间" prop="latestEndTime">
      <el-input v-model="dataForm.latestEndTime" placeholder="最晚会议结束时间"></el-input>
    </el-form-item>
    <el-form-item label="参会人员及其签到情况" prop="participants">
      <el-input v-model="dataForm.participants" placeholder="参会人员及其签到情况"></el-input>
    </el-form-item>
    <el-form-item label="会议报告地址" prop="reportAddress">
      <el-input v-model="dataForm.reportAddress" placeholder="会议报告地址"></el-input>
    </el-form-item>
    <el-form-item label="预定周期，cron格式（用于按日、周、月预定）" prop="scheduledPeriod">
      <el-input v-model="dataForm.scheduledPeriod" placeholder="预定周期，cron格式（用于按日、周、月预定）"></el-input>
    </el-form-item>
    <el-form-item label="启用状态（0为禁用，1为启用）" prop="meetingStatus">
      <el-input v-model="dataForm.meetingStatus" placeholder="启用状态（0为禁用，1为启用）"></el-input>
    </el-form-item>
    <el-form-item label="会议室id" prop="roomId">
      <el-input v-model="dataForm.roomId" placeholder="会议室id"></el-input>
    </el-form-item>
    <el-form-item label="会议描述" prop="meetingDescription">
      <el-input v-model="dataForm.meetingDescription" placeholder="会议描述"></el-input>
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
          meetingId: 0,
          title: '',
          startTime: '',
          endTime: '',
          latestEndTime: '',
          participants: '',
          reportAddress: '',
          scheduledPeriod: '',
          meetingStatus: '',
          roomId: '',
          meetingDescription: ''
        },
        dataRule: {
          title: [
            { required: true, message: '会议标题不能为空', trigger: 'blur' }
          ],
          startTime: [
            { required: true, message: '会议开始时间不能为空', trigger: 'blur' }
          ],
          endTime: [
            { required: true, message: '预计会议结束时间不能为空', trigger: 'blur' }
          ],
          latestEndTime: [
            { required: true, message: '最晚会议结束时间不能为空', trigger: 'blur' }
          ],
          participants: [
            { required: true, message: '参会人员及其签到情况不能为空', trigger: 'blur' }
          ],
          reportAddress: [
            { required: true, message: '会议报告地址不能为空', trigger: 'blur' }
          ],
          scheduledPeriod: [
            { required: true, message: '预定周期，cron格式（用于按日、周、月预定）不能为空', trigger: 'blur' }
          ],
          meetingStatus: [
            { required: true, message: '启用状态（0为禁用，1为启用）不能为空', trigger: 'blur' }
          ],
          roomId: [
            { required: true, message: '会议室id不能为空', trigger: 'blur' }
          ],
          meetingDescription: [
            { required: true, message: '会议描述不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.meetingId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.meetingId) {
            this.$http({
              url: this.$http.adornUrl(`/intelligent/meeting/info/${this.dataForm.meetingId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.title = data.meeting.title
                this.dataForm.startTime = data.meeting.startTime
                this.dataForm.endTime = data.meeting.endTime
                this.dataForm.latestEndTime = data.meeting.latestEndTime
                this.dataForm.participants = data.meeting.participants
                this.dataForm.reportAddress = data.meeting.reportAddress
                this.dataForm.scheduledPeriod = data.meeting.scheduledPeriod
                this.dataForm.meetingStatus = data.meeting.meetingStatus
                this.dataForm.roomId = data.meeting.roomId
                this.dataForm.meetingDescription = data.meeting.meetingDescription
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
              url: this.$http.adornUrl(`/intelligent/meeting/${!this.dataForm.meetingId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'meetingId': this.dataForm.meetingId || undefined,
                'title': this.dataForm.title,
                'startTime': this.dataForm.startTime,
                'endTime': this.dataForm.endTime,
                'latestEndTime': this.dataForm.latestEndTime,
                'participants': this.dataForm.participants,
                'reportAddress': this.dataForm.reportAddress,
                'scheduledPeriod': this.dataForm.scheduledPeriod,
                'meetingStatus': this.dataForm.meetingStatus,
                'roomId': this.dataForm.roomId,
                'meetingDescription': this.dataForm.meetingDescription
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
