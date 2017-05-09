#!/bin/bash
file="example.com"
n_max=$(ls -1 "${file}"* | egrep -o "[0-9]+$" | sort -rn | head -n 1)
cp "${file}" "${file}.$((n_max+1))"
NAME=$(echo "${file}.$((n_max+1))")
echo ${NAME} > example.properties
