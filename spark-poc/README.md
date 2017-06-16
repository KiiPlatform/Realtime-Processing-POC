## Prepare user, group and environment variable

1. Obtain access token (regular kii user, not necessary an Admin user). Please refer to [this tutorial](http://docs.kii.com/en/guides/cloudsdk/rest/managing-users/login/sign-in/) to get access token.

2. Create group on Kii developer portal, name the group as `vehicle-monitoring` and set your newly created user as the group owner. [This tutorial](http://docs.kii.com/en/guides/devportal/application_manipulation/data_browsing/group-console/#adding)
 will help you to guide.

3. Obtain NRE certificates credentials and related info (provided by Kii representatives)

4. Execute `kii-setup.snb` on your `spark-notebook` installation. Please check [spark-notebook-nre](https://github.com/KiiPlatform/spark-notebook-nre) repocitory for more detail. 

4. Create `.env` file and put lines bellow.

    ```
    export TOPIC_ID='#kafka_topic_id_from_nre' 
    export GROUP_ID='#kafka_group_id_from_nre' 
    export KAFKA_BOOTSTRAP_SERVERS='#bootstrap_servers' 
    export TRUSTSTORE_PASSWORD='#truststore_jks_password'
    export KEYSTORE_PASSWORD='#store_jks_password'
    export KEY_PASSWORD='#trustore_jks_password'
    export KII_HOST='#kii_app_host'
    export KII_ACCESS_TOKEN='kii_access_token'
    export KII_APP_ID='#kii_app_id'
    export KII_APP_KEY='7b3d7fe30777d53749ace9b15caf6c5f'
    ```

5. Execute command bellow.

    ```bash
    source .env
    ```

## How to Build

Build with sbt.

```
sbt assembly
```

# Run on Standalone Spark

Make sure your spark bin is on your system path.
If your spark installation is `/usr/local/spark` then you can add bellow to your `.bash_profile`.

```
export PATH = $PATH:/usr/local/spark/bin 
```

Submit the NRE bridge 

```
sh submit.sh com.kii.nre.spark.KiiNREBridge
```


