Para iniciar um Peer:
java -cp <jar_file> sdis.backupsystem.Peer <peer_id> <ip_MC> <port_MC> <ip_MDB> <port_MDB> <ip_MDR> <port_MDR>
<jar_file> - path to jar file
<peer_id> - id do peer
<ip_MC> - ip para socket "Control Channel"
<port_MC> - porta para socket "Control Channel"
<ip_MDB> - ip para socket "Data Backup"
<port_MDB> - porta para socket "Data Backup"
<ip_MDR> - ip para socket "Data Restore"
<port_MDR> - porta para socket "Data Restore"

Para iniciar a TestApp:

java -cp <jar_file> sdis.backupsystem.Peer_initiator <peer_id> <ip_MC> <port_MC> <ip_MDB> <port_MDB> <ip_MDR> <port_MDR> <Protocol> <File> <Replication>
<jar_file> - path to jar file
<peer_id> - id do peer
<ip_MC> - ip para socket "Control Channel"
<port_MC> - porta para socket "Control Channel"
<ip_MDB> - ip para socket "Data Backup"
<port_MDB> - porta para socket "Data Backup"
<ip_MDR> - ip para socket "Data Restore"
<port_MDR> - porta para socket "Data Restore"
<Protocol> - protocolo
<File> - ficheiro
<Replication> - grau de replicação do ficheiro