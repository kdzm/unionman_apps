#/bin/bash
rm -rf sor_update.zip update.zip
cd  ./file && zip -qry ../sor_update.zip . && cd ..
cd ./file && zip -q ../sor_update.zip META/* && cd ..
java  -Xms256m -Xmx2048m  -jar signapk.jar  -w  testkey.x509.pem  testkey.pk8 sor_update.zip update.zip
rm -rf sor_update.zip
