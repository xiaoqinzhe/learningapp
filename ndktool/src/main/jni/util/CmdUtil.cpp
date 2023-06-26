#include <stdio.h>
#include <string.h>
#include "../logutil.h"

#define CMD_RESULT_BUF_SIZE 10240

/*
 * cmd：待执行命令
 * result：命令输出结果
 * 函数返回：0 成功；-1 失败；
 */
int ExecuteCMD(const char *cmd, char *result, int buf_size)
{
    int iRet = -1;
    char buf_ps[CMD_RESULT_BUF_SIZE];
    char ps[CMD_RESULT_BUF_SIZE] = {0};
    FILE *ptr;

    strcpy(ps, cmd);

    LOGD("ExecuteCMD cmd: %s", ps);

    if((ptr = popen(ps, "r")) != nullptr)
    {
        while(fgets(buf_ps, sizeof(buf_ps), ptr) != nullptr)
        {
            LOGD("ExecuteCMD fgets %s ", buf_ps);
           strcat(result, buf_ps);
           if(strlen(result) >= buf_size)
           {
               LOGD("ExecuteCMD len=%d", strlen(result));
               break;
           }
        }
        LOGD("ExecuteCMD fgets %s ", buf_ps);
        pclose(ptr);
        ptr = nullptr;
        iRet = 0;  // 处理成功
    }
    else
    {
        LOGD("ExecuteCMD popen %s error", ps);
        iRet = -1; // 处理失败
    }

    return iRet;
}