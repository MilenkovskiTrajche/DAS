import subprocess
import time

# Define the timeout for the first script
timeout = 120  # 2 minutes = 120 seconds

scripts = [
    "webscrapper.py",
    "weeklyOscToDB.py",
    "weeklyMovAvgToDB.py",
    "monthlyOscillatorsToDB.py",
    "monthlyMovAvgToDB.py"
]

# Run webscrapper.py with a timeout of 2 minutes
try:
    print(f"Running {scripts[0]} with a 2-minute timeout...")
    subprocess.run(["python", scripts[0]], timeout=timeout)
    print(f"{scripts[0]} finished!")
except subprocess.TimeoutExpired:
    print(f"{scripts[0]} timed out after {timeout} seconds!")

# Run the rest of the scripts without timeout
for script in scripts[1:]:
    print(f"Running {script}...")
    subprocess.run(["python", script])
    print(f"{script} finished!")
