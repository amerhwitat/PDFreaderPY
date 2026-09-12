#!/usr/bin/env python3
from pathlib import Path
import argparse,platform,shutil,subprocess,sys,time,shlex
R=Path(__file__).resolve().parents[1]
def L(s,m): print(f'[{time.strftime("%H:%M:%S")}] [{s}] {m}',flush=True)
def W(n): return shutil.which(n)
def X(c,d=False):
 L('COMMAND',' '.join(shlex.quote(str(x)) for x in c))
 if d:return 0
 p=subprocess.Popen([str(x) for x in c],cwd=R,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
 for x in p.stdout: print(x.rstrip(),flush=True)
 return p.wait()
def main():
 a=argparse.ArgumentParser();a.add_argument('--dry-run',action='store_true');a.add_argument('--only',choices=['all','python','java','node','native','sql'],default='all');a.add_argument('--onefile',action='store_true');a.add_argument('--python');a=a.parse_args();L('BUILD',f'{R.name} / {platform.system()} / {platform.machine()}')
 if a.only in ('all','python') and (a.python or list(R.rglob('*.py'))):
  if W('python'):
   ps=[R/a.python] if a.python else [p for p in R.rglob('*.py') if p.name not in {'__init__.py','setup.py'} and '.git' not in p.parts and 'build' not in p.parts]
   if ps:
    q=[sys.executable,'-m','PyInstaller','--noconfirm','--clean']+(['--onefile'] if a.onefile else [])+[str(ps[0])]
    if X(q,a.dry_run):return 1
  else:L('SKIP','Python missing')
 if a.only in ('all','java') and (list(R.rglob('*.java')) or (R/'pom.xml').exists() or (R/'build.gradle').exists()):
  q=['mvn','-B','test','package'] if (R/'pom.xml').exists() and W('mvn') else ['gradle','build'] if (R/'build.gradle').exists() and W('gradle') else ['javac',*[str(p) for p in R.rglob('*.java')]] if W('javac') else []
  if q and X(q,a.dry_run):return 1
 if a.only in ('all','node') and (R/'package.json').exists():
  pm='pnpm' if (R/'pnpm-lock.yaml').exists() and W('pnpm') else 'yarn' if (R/'yarn.lock').exists() and W('yarn') else 'npm'
  if W(pm):
   if X((['npm','ci'] if pm=='npm' and (R/'package-lock.json').exists() else [pm,'install']),a.dry_run) or X([pm,'run','build'],a.dry_run):return 1
 if a.only in ('all','native') and (R/'CMakeLists.txt').exists() and W('cmake'):
  b=R/'build'/'cmake';b.mkdir(parents=True,exist_ok=True)
  if X(['cmake','-S',str(R),'-B',str(b),'-DCMAKE_BUILD_TYPE=Release'],a.dry_run) or X(['cmake','--build',str(b),'--config','Release','--parallel'],a.dry_run):return 1
 if a.only in ('all','sql'):L('DATABASE',f'{len(list(R.rglob("*.sql")))} SQL scripts discovered; use configured native clients')
 L('BUILD','DONE');return 0
if __name__=='__main__':raise SystemExit(main())
