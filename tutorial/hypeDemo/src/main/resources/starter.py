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

def startJar():
    os.system("cd /proj/; nohup java -jar " + jar_name + " &")
    naplozas("Parancs Lefuttatva: cd /proj/; nohup java -jar " + jar_name + " &")


def findProcessIdByName(processName):
    # Vegigmegy a futo folyamatokon
    for proc in psutil.process_iter():
        try:
            # Megnezi hogy benne van e a jar_name a folyamat neveben
            if processName in proc.cmdline():
                return True
        except (psutil.NoSuchProcess, psutil.AccessDenied, psutil.ZombieProcess):
            pass
    return False

git_valtozas_volt_e = git_pull_change(repo_path)

if (git_valtozas_volt_e):

    # Letrehozza az uj (friss) .jar file-t, skippeli a testeket
    maven_return_val = os.system("cd " + repo_path + java_project_path + "; mvn clean install -DskipTests")

    # Ha hiba tortent akkor kilep
    if (maven_return_val != 0):
        naplozas("MVN clean install sikertelen")
        sys.exit()

    # Ha mar letezik a copy deatination-ben a .jar file, kitorli
    if (os.path.exists(project_path + "/" + jar_name)):
        os.remove(project_path + "/" + jar_name)

    # Majd átmásolja
    shutil.copyfile(repo_path + java_project_path + "/target/" + jar_name, project_path + "/" + jar_name)

    fut_e = findProcessIdByName(jar_name)

    # Ha fut jar_name nevu folyamat, megoli
    if (fut_e):
        pid_number = subprocess.check_output(["pgrep","-f", jar_name]).decode("utf-8")
        os.system("kill " + pid_number)
        naplozas("Parancs Lefuttatva: kill " + pid_number)

    # Elindul a .jar file nohup-al
    startJar()
else:
    # Ha nincs git valtoztatas
    fut_e = findProcessIdByName(jar_name)
    naplozas("Futo-e a jar folyamat?: " + str(fut_e))

    # Ha nem fut a program elinditja.
    if (fut_e == False):
        naplozas("Nincs Git változtatás. A program nem fut. Elindítás...")

        # Program elinditasa
        startJar()
    else:
        naplozas("Nincs Git változtatás. A program fut. Kilépés...")
    sys.exit()
