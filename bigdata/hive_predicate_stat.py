#!/usr/bin/python

import datetime
import sys
import re
import platform
import glob
import os

log="/var/log/hive/hiveserver2*.log*"
#log = "/var/log/hive/hiveserver2Interactive.log.2019-08-02"

tbl="pos_rec_itm"
schem="rdw"
sql_type="select"
exclude_pred="calday"

#-----

query_start='queryId=hive_'

#CREATE EXTERNAL TABLE `stg.hive_queries`( tbl string,
#node string,
#calday string,
#sql string) 
#ROW FORMAT DELIMITED FIELDS TERMINATED BY '|' LOCATION '/apps/hive/warehouse/stg/hive_queries/';

def process_log(p_log, p_out):
    print str(datetime.datetime.now()) + " " + p_log + " START "
    
    file = open(p_log, "r") 

    node = platform.node()
    
    sqls = file.read().split(query_start)

    #elems with pos_rec_itm
    sqls_fnd = list(filter(lambda x: x.lower().find(tbl) >=0 and x.lower().find(schem) >=0 and x.lower().find(sql_type) >=0 , sqls))
    
    has_lines = 0

    with open(p_out, 'w') as f:
        has_lines = 0
        for item in sqls_fnd:
            litem=item.lower()
            froms = litem.split('where')
            
            #after where
            froms_fnd = list(filter(lambda x: x.find(tbl) >=0 and x.find(schem) >=0, froms))
            
            for fitem in froms_fnd:
                #before group by or next line (what before)
                fn=fitem.find("group")
                fni=fitem.find("info")
                if fn == -1:
                    if fni > -1:
                        fn = fn - 24
                elif fni > -1 and fni < fn:
                    fn = fni
                    
                #if has calday predicate
                if fn > -1 and fitem[0:fn].find(exclude_pred) > -1:
                    
                    #sanitize sql
                    sqlq = fitem[0:fn].strip().replace("\n", " ").replace("\r", " ").replace("\t", " ")
                    
                    
                    #split where to predicate
                    predicats = sqlq.split('and')
                    #predicate without calday
                    predicats_fltr = list(filter(lambda x: x.find(exclude_pred) == -1 and x.find("'201") == -1, predicats))
                    predicats_fltr.sort()
                    predicats_sql = ' and '.join(predicats_fltr)
                    
                    predicats_sql = re.sub(r'[ ][ ]{2,}', ' ', predicats_sql).replace('  ', ' ').strip()
                    
                    if len(predicats_sql) > 0:
                        f.write(schem + "." + tbl + "-"+str(fitem.find("group")))
                        f.write("|")
                        f.write(node)
                        f.write("|")
                        f.write(item[0:14])
                        f.write("|")
                        f.write(predicats_sql)
                        f.write("\n")
                        has_lines = 1
                              
        f.close()
        if has_lines > 0:
            os.system("hadoop fs -put -f " + p_out + " /apps/hive/warehouse/stg/hive_queries/" + p_out)
        os.system("rm -f " + p_out)
    print str(datetime.datetime.now()) + " " + p_log + " END "
                    
#-----


print str(datetime.datetime.now()) + " " + log + " START "
node = platform.node()

for f in glob.glob(log):
    process_log(f, node + "_" + f[f.rfind('.')+1::].replace("-", "")+".csv")


print str(datetime.datetime.now()) + " " + log + " END "
