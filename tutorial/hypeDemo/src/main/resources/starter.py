import datetime
import os.path
import time
import subprocess
import sys
import git
import shutil
import psutil

project_path = "/proj"
jar_name = "hypeDemo-0.0.1-SNAPSHOT.jar"
repo_path = "/opt/DuszaWorkshop-KapOS"
java_project_path = "/tutorial/hypeDemo"

t = time.localtime()
# Ha van valtozas akkor true
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
    current_time = time.strftime("%H:%M:%S", t)
    print(str(current_time) + " >>> " + szoveg)


# Processek megkeresese
def findProcessIdByName(processName):
    listOfProcessObjects = []
    # Iterate over the all the running process
    for proc in psutil.process_iter():
        try:
            pinfo = proc.as_dict(attrs=['pid', 'name', 'create_time'])
            # Check if process name contains the given name string.
            if processName.lower() in pinfo['name'].lower():
                listOfProcessObjects.append(pinfo)
        except (psutil.NoSuchProcess, psutil.AccessDenied, psutil.ZombieProcess):
            pass
    return listOfProcessObjects


git_valtozas_volt_e = git_pull_change(repo_path)

if (git_valtozas_volt_e):
    # Letrehozza az uj (friss) .jar file-t
    maven_return_val = os.system("cd " + repo_path + java_project_path + "; mvn clean install")
    # Ha hiba tortent akkor kilep
    if (maven_return_val != 0):
        naplozas("MVN clean install sikertelen")
        sys.exit()
    # Ha mar letezik a copy deatination-ben a .jar file, kitorli
    if (os.path.exists(project_path + "/" + jar_name)):
        os.remove(project_path + "/" + jar_name)
    # Majd átmásolja
    shutil.copyfile(repo_path + java_project_path + "/target/" + jar_name, project_path + "/" + jar_name)
else:
    naplozas("A program fut és változás sincs. Marad minden ahogy volt. Kilépés...")
    sys.exit()
listOfProcessIds = findProcessIdByName(jar_name)

# Ha volt git valtozas, es van jar_name nevu folyamat, megoli
if len(listOfProcessIds) > 0:
    naplozas("Fut a folyamat, leallitom")
    if(git_valtozas_volt_e):
        pid_number = subprocess.check_output("pgrep -f " + jar_name)
        os.system("kill " + pid_number)
        naplozas("Parancs Lefuttatva: kill " + pid_number)
    # Ha nincs git valtozas, a kod kilep
    else:
        naplozas("A program fut és változás sincs. Marad minden ahogy volt.")
        sys.exit()
else:
    naplozas("A folyamat nem futott.")

# Elindul a .jar file nohup-al
os.system("cd /proj/; nohup java -jar " + jar_name + " &")
naplozas("Parancs Lefuttatva: cd /proj/; nohup java -jar " + jar_name + " &")

# AAA