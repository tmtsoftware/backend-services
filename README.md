# Testkit for running TMT services
This repository will help to run any ESW / CSW services and/or component(s) by making use of ESW-testkit.

### Services that can be started are listed below
    1. Location (It is started by default)
    2. Gateway
    3. Config
    4. Alarm
    5. AAS (Auth service)
    6. Event

### Test HCD component can be started with dummy HCD handlers.
### Sequencer can be started in simulation mode.

---

### 1. To start Gateway, AAS and Alarm services
> cs launch --channel https://raw.githubusercontent.com/tmtsoftware/apps/master/apps.json backend-testkit-services:5c8168a -- start -s Gateway -s AAS -s Alarm

    1. To start only location service, run the above without any service name argument(-s)
    2. Possible values that can be passed to start command as arguments are listed above.

### 2. To start Sequencer in **ESW** subsystem with **clearsky** observing mode
> cs launch --channel https://raw.githubusercontent.com/tmtsoftware/apps/master/apps.json backend-testkit-sequencer:5c8168a -- start -s ESW -m clearsky

    1. `-s` represents subsystem for the sequencer
    2. `-m` represents the mode in which the sequencer is to be ran in.

### 3. To start a standalone testHcd Component with provided testHcd.conf
> cs launch --channel https://raw.githubusercontent.com/tmtsoftware/apps/master/apps.json backend-testkit-component:5c8168a -- start --local testHcd.conf --standalone
    
    1. standalone flag is optional, if not provided testHcd will run in container mode
    2. local flag is optional, if not provided config will be fetched from config server.
