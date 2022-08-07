//
// Created by 1 on 2022/8/3.
//

#include "FileScan.h"
#include<string>
#include<queue>
#include<dirent.h>
#include<sys/time.h>
#include<ftw.h>
#include<sys/stat.h>
#include "logutil.h"

long getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv, nullptr);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

int getDirSizeRec(const std::string& path) {
    struct dirent *iter;
    int fileCount = 0;
    DIR *dir = opendir(path.c_str());
    if (dir == nullptr) {
//        LOGD("getDirSize dir null %s", path.c_str());
    } else {
        while ((iter = readdir(dir)) != nullptr) {
            if (strcmp(iter->d_name, ".") == 0 || strcmp(iter->d_name, "..") == 0) {
                continue;
            }
            std::string subfile = path + iter->d_name + "/";
//            LOGD("getDirSize %s %d", subfile.c_str(), iter->d_type);
            if (iter->d_type == DT_DIR) {
                fileCount += getDirSizeRec(subfile);
            }
            ++fileCount;
        }
        closedir(dir);
    }
    return fileCount;
}

int staticFileCount;

int trans(const char* pathname, const struct stat* stat, int type, struct FTW* ftw) {
    ++staticFileCount;
    return 0;
}

void nftwGetDirSize(const std::string& path) {
    staticFileCount = 0;
    nftw(path.c_str(), trans, 20, FTW_PHYS);
}

int64_t listdir(const char *fileName, int32_t mode, int32_t docType, int64_t time, uint8_t subdirs) {
    int64_t value = 0;
    DIR *dir;
    struct stat attrib;
    if ((dir = opendir(fileName)) != NULL) {
        char buff[4096];
        struct dirent *entry;
        while ((entry = readdir(dir)) != NULL) {
            char *name = entry->d_name;
            size_t len = strlen(name);
            if (strcmp(name, ".") == 0 || strcmp(name, "..") == 0) {
                continue;
            }
            if ((docType == 1 || docType == 2) && len > 4) {
                if (name[len - 4] == '.' && (
                        ((name[len - 3] == 'm' || name[len - 3] == 'M') && (name[len - 2] == 'p' || name[len - 2] == 'P') && (name[len - 1] == '3')) ||
                        ((name[len - 3] == 'm' || name[len - 3] == 'M') && (name[len - 2] == '4') && (name[len - 1] == 'a' || name[len - 1] == 'A'))
                )) {
                    if (docType == 1) {
                        continue;
                    }
                } else if (docType == 2) {
                    continue;
                }
            }
            strncpy(buff, fileName, 4095);
            strncat(buff, "/", 4095);
            strncat(buff, entry->d_name, 4095);
            if (entry->d_type == DT_DIR) {
                if (subdirs) {
                    value += listdir(buff, mode, docType, time, subdirs);
                }
            } else {
                stat(buff, &attrib);
                if (mode == 0) {
                    value += 512 * attrib.st_blocks;
                } else if (mode == 1) {
                    if (attrib.st_atim.tv_sec != 0) {
                        if (attrib.st_atim.tv_sec < time) {
                            remove(buff);
                        }
                    } else {
                        if (attrib.st_mtim.tv_sec < time) {
                            remove(buff);
                        }
                    }
                }
            }
        }
        closedir(dir);
    }
    return value;
}

int getDirSize(const std::string& path) {
    long startTime = getCurrentTime();
//    nftwGetDirSize(path);
//    long count = staticFileCount;
    long count = listdir(path.c_str(),0,0,0,true);
    LOGD("getDirSize end. fileCount = %d, duration=%ld", count
        , getCurrentTime() - startTime);
    return 1;
}

int getDirSize2(std::string path) {
    LOGD("getDirSize start %s", path.c_str());
    std::queue<std::string> dirPaths;
    dirPaths.push(path);
    struct dirent *iter;
    std::string curPath;
    int fileCount = 0;
    long startTime = getCurrentTime();
    while (!dirPaths.empty()) {
        curPath = dirPaths.front();
        dirPaths.pop();
        DIR *dir = opendir(curPath.c_str());
        if (dir == nullptr) {
            LOGD("getDirSize dir null %s", curPath.c_str());
        } else {
            while ((iter = readdir(dir)) != nullptr) {
                if (strcmp(iter->d_name, ".") == 0 || strcmp(iter->d_name, "..") == 0) {
                    continue;
                }
                std::string subfile = curPath + iter->d_name + "/";
                LOGD("getDirSize %s %d", subfile.c_str(), iter->d_type);
                if (iter->d_type == DT_DIR) {
                    dirPaths.push(subfile);
                }
                ++fileCount;
            }
        }
        closedir(dir);
    }
    LOGD("getDirSize end. fileCount = %d, duration=%ld", fileCount
         , getCurrentTime() - startTime);
    return 0;
}