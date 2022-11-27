import datetime
import os.path
import signal
import sys

import git
import shutil
import psutil


project_path = "/proj"
jar_name = "hypeDemo-0.0.1-SNAPSHOT.jar"
repo_path = "/opt/DuszaWorkshop-KapOS"
java_project_path = "/tutorial/hypeDemo"


def git_pull_change(path):
    repo = git.Repo(path)
    current = repo.head.commit

    repo.remotes.origin.pull()

    if current == repo.head.commit:
        naplozas("A repo nem valtozott")
        return False
    else:
        naplozas("A repo valtozott.")
        return True


def naplozas(szoveg):
    print(str(datetime.time) + "\t" + szoveg)

def findProcessIdByName(processName):
    '''
    Get a list of all the PIDs of a all the running process whose name contains
    the given string processName
    '''
    listOfProcessObjects = []
    #Iterate over the all the running process
    for proc in psutil.process_iter():
       try:
           pinfo = proc.as_dict(attrs=['pid', 'name', 'create_time'])
           # Check if process name contains the given name string.
           if processName.lower() in pinfo['name'].lower() :
               listOfProcessObjects.append(pinfo)
       except (psutil.NoSuchProcess, psutil.AccessDenied , psutil.ZombieProcess) :
           pass
    return listOfProcessObjects


git_valtozas_volt_e=git_pull_change(repo_path)
if (git_valtozas_volt_e):
    maven_return_val = os.system("cd " + repo_path + java_project_path+"; maven clean install")
    if (maven_return_val != 0):
        naplozas("maven clean install sikertelen")
        sys.exit()
    if(os.path.exists(project_path + "/" + jar_name)):
        os.remove(project_path + "/" + jar_name)
    shutil.copyfile(repo_path+java_project_path+"/target/"+jar_name, project_path+"/"+jar_name)

listOfProcessIds = findProcessIdByName(jar_name)
if len(listOfProcessIds) > 0:
   naplozas("Fut a folyamat, leallitom")
   if(git_valtozas_volt_e):
       for elem in listOfProcessIds:
           processID = elem['pid']
           os.kill(processID, signal.SIGINT)
   else:
        naplozas("A program fut és változás sincs. Marad minden ahogy volt.")
        sys.exit()
else :
   naplozas("A folyamat nem futott")

os.system("cd " + project_path + "; nohup java -jar "+ project_path+"/"+jar_name +" &")
naplozas("nohup java -jar "+ project_path+"/"+jar_name +" &")