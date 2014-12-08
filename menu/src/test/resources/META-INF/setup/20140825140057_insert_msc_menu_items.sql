
TRUNCATE TABLE menu_items;
INSERT INTO menu_items(id, parent_id, name,          state,                           position) VALUES
(1,                       null,     '用户',        'index.user', 0),
(2,                       null,     'SLA',         'index.sla', 2),
(3,                       null,     '合同',        'index.contract', 1),
(4,                       null,     '故障',        '',                               3),
(5,                       4,     'MSC故障',    'index.list-trouble-ticket', 0),
(6,                       4,     'MSP故障',    'index.msp-list-trouble-ticket', 1),
(7,                       5,     'MSC故障1',   'index.list-trouble-ticket1', 0),
(8,                       5,     'MSC故障2',   'index.list-trouble-ticket2', 1),
(9,                       6,     'MSP故障1',   'index.msp-list-trouble-ticket1', 0),
(10,                      9,     'MSP故障1-1', 'index.msp-list-trouble-ticket1-1', 0);
