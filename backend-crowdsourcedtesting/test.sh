#!/bin/bash
# 判断传入的参数的个数是不是一个
if [ ! $# -eq 1  ];then
  echo param error!
  exit 1
fi

# 判断目录是不是已经存在，如果不存在则创建，存在则输出“dir exist” 
dirname=$1
echo "the dir name is $dirname"
if [ ! -d $dirname  ];then
  mkdir $dirname
else
  echo dir exist
fi

