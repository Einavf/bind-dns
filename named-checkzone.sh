#!/bin/bash
sudo named-checkzone full example*.com.*
if [ $? -ne 0 ];
then
echo "************************************************************************************"
echo named-checkzone failed due to errors in the file, please fix and run the job again
echo "*************************************************************************************"
exit 1
fi