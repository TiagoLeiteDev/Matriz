-- Explicit human-approved reconciliation, preserving IDs, nulls and relationships.
ALTER TABLE tarefa DROP CONSTRAINT tarefa_estado_check;
ALTER TABLE tarefa DROP CONSTRAINT tarefa_prioridade_check;

UPDATE tarefa SET estado = CASE estado
    WHEN 'Por Fazer' THEN 'TODO'
    WHEN 'Em Curso' THEN 'IN_PROGRESS'
    WHEN 'Em Revisão' THEN 'IN_PROGRESS'
    WHEN 'Concluída' THEN 'DONE'
    ELSE estado END;
UPDATE tarefa SET prioridade = CASE prioridade
    WHEN 'Baixa' THEN 'LOW'
    WHEN 'Média' THEN 'MEDIUM'
    WHEN 'Alta' THEN 'HIGH'
    WHEN 'Crítica' THEN 'HIGH'
    ELSE prioridade END;

ALTER TABLE tarefa ADD CONSTRAINT tarefa_estado_check CHECK (estado IN ('TODO', 'IN_PROGRESS', 'DONE'));
ALTER TABLE tarefa ADD CONSTRAINT tarefa_prioridade_check CHECK (prioridade IN ('LOW', 'MEDIUM', 'HIGH'));
ALTER TABLE tarefa ALTER COLUMN estado SET DEFAULT 'TODO';
ALTER TABLE tarefa ALTER COLUMN prioridade SET DEFAULT 'MEDIUM';
